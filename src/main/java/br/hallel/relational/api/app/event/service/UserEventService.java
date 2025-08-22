package br.hallel.relational.api.app.event.service;

import br.hallel.relational.api.app.event.dto.*;
import br.hallel.relational.api.app.event.exception.EventIllegalArumentException;
import br.hallel.relational.api.app.event.exception.EventParticipationException;
import br.hallel.relational.api.app.event.model.*;
import br.hallel.relational.api.app.event.repository.EventParticipationRepository;
import br.hallel.relational.api.app.event.repository.EventRepository;
import br.hallel.relational.api.app.event.repository.EventTransactionRepository;
import br.hallel.relational.api.app.payment.checkout_transparent.client.MercadoPagoClient;
import br.hallel.relational.api.app.user.exceptions.UserNotFoundException;
import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserEventService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventParticipationRepository eventParticipationRepository;
    private final EventTransactionRepository eventTransactionRepository;
    private final MercadoPagoClient mercadoPagoClient;

    public EventParticipationResponse joinTheEvent(UUID userId, EventParticipateDTO dto) {
        Event event = this.eventRepository.findById(dto.getEventId()).orElseThrow(
                () -> new EventIllegalArumentException("Event with id " + dto.getEventId() + " does not exist.")
        );
        User user = this.userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User with id " + userId + " does not exist.")
        );

        boolean alreadyParticipating = eventParticipationRepository.existsByUserAndEvent(user, event);
        if (alreadyParticipating) {
            log.warn("Usuário ID {} já está participando do evento ID {}. Lançando exceção.", userId,
                    dto.getEventId());
            throw new EventIllegalArumentException("User already participating in this event.");
        }

        EventParticipation eventParticipation = new EventParticipation();
        eventParticipation.setUser(user);
        eventParticipation.setEvent(event);
        eventParticipation.setUserFunctionInEvent(UserFunctionInEvent.PARTICIPANTE);
        eventParticipation.setHasParticipated(false);

        String qrCodeBase64 = null;

//        if (!event.getItsFree() || event.getValue() > 0) {
//            try {
//                String fullName = user.getName();
//                String firstName = "";
//                String lastName = "";
//
//                if (fullName != null && !fullName.isEmpty()) {
//                    String[] names = fullName.split(" ");
//                    if (names.length > 0) {
//                        firstName = names[0];
//                    }
//                    if (names.length > 1) {
//                        lastName = String.join(" ", java.util.Arrays.copyOfRange(names, 1, names.length));
//                    }
//                }
//                CreatePixPaymentRequestDTO paymentRequestDTO = new CreatePixPaymentRequestDTO(
//                        BigDecimal.valueOf(event.getValue()),
//                        event.getTitle(),
//                        user.getEmail(),
//                        firstName,
//                        lastName,
//                        user.getCpf()
//                );
//
//                Payment payment = mercadoPagoClient.createPixPayment(paymentRequestDTO);
//
//                // Verificação de segurança adicional para evitar NPE
//                if (payment != null && payment.getPointOfInteraction() != null &&
//                        payment.getPointOfInteraction().getTransactionData() != null) {
//                    eventParticipation.setStatusPaymentEventParticipation(StatusPaymentEventParticipation.PENDENTE);
//                    eventParticipation.setPixTxid(payment.getPointOfInteraction().getTransactionData().getQrCode());
//                    qrCodeBase64 = payment.getPointOfInteraction().getTransactionData().getQrCodeBase64();
//
//                    log.info("Pagamento Pix criado com sucesso para o usuário ID {}. TXID: {}", dto.getUserId(),
//                            eventParticipation.getPixTxid());
//                } else {
//                    log.error("Resposta do Mercado Pago incompleta, dados de transação ou de interação nulos.");
//                    throw new RuntimeException("Erro ao processar a resposta do Mercado Pago.");
//                }
//
//            } catch (MPException | MPApiException e) {
//                log.error("Erro ao criar pagamento Pix no Mercado Pago: {}", e.getMessage(), e);
//                throw new RuntimeException("Erro ao criar pagamento Pix. Por favor, tente novamente.", e);
//            }
//        } else {
//            eventParticipation.setStatusPaymentEventParticipation(StatusPaymentEventParticipation.PAGO);
//        }
        eventParticipation.setStatusPaymentEventParticipation(StatusPaymentEventParticipation.PAGO);
        eventParticipation.setPaidDate(Instant.now().atOffset(ZoneOffset.UTC));
        EventParticipation participationSaved = eventParticipationRepository.save(eventParticipation);
        log.info("Participação do evento salva no banco de dados com ID: {}", participationSaved.getId());
        return new EventParticipationResponse().toEventParticipation(participationSaved, qrCodeBase64);

    }

    public EventPayParticipationDetails detailsPayAnEvent(UUID userId, EventParticipateDTO dto) {
        Event event = this.eventRepository.findById(dto.getEventId()).orElseThrow(
                () -> new EventIllegalArumentException("Event with id " + dto.getEventId() + " does not exist.")
        );
        User user = this.userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User with id " + userId + " does not exist.")
        );

        boolean alreadyParticipating = eventParticipationRepository.existsByUserAndEvent(user, event);
        if (!alreadyParticipating) {
            log.warn("Usuário ID {} não está participando do evento ID {}. Lançando exceção.", userId,
                    dto.getEventId());
            throw new EventIllegalArumentException("User does not participate in the event.");
        }

        EventParticipation participation = this.eventParticipationRepository.
                findByUser_IdAndEvent_Id(userId, dto.getEventId()).orElseThrow(
                        () -> new EventParticipationException("Not Found participation. Verify if he exists in the " +
                                "event")
                );

        if (participation.getPixTxid() == null) {
            throw new EventParticipationException("This user no have an pix to pay");
        }

        if(participation.getStatusPaymentEventParticipation() == StatusPaymentEventParticipation.PAGO){
            throw new EventParticipationException("This user already pay the event.");
        }

        byte[] pixTxidBytes = participation.getPixTxid().getBytes();

        return new EventPayParticipationDetails(Base64.getEncoder().encodeToString(pixTxidBytes), participation.getPixTxid(),
                event.getValue());
    }


    public boolean leaveTheEvent(UUID participationId) {
        EventParticipation participation = eventParticipationRepository.findById(participationId)
                .orElseThrow(() -> new EventIllegalArumentException(
                        "Participation Event with id " + participationId + " does not exist."
                ));

        List<EventTransaction> transactions =
                eventTransactionRepository.findAllByEvent_Id(participation.getEvent().getId());
        transactions.stream()
                .filter(t -> t.getDesciption().contains(participation.getUser().getName()))
                .forEach(eventTransactionRepository::delete);

        eventParticipationRepository.delete(participation);
        return true;
    }

    public EventParticipationResponse editParticipationEvent(UUID participationId, EventParticipationDTO dto) {

        EventParticipation participation = eventParticipationRepository.findById(participationId)
                .orElseThrow(() -> new EventIllegalArumentException(
                        "Participation with id " + participationId + " does not exist."
                ));

        if (dto.statusPaymentEventParticipation() != null) {
            participation.setStatusPaymentEventParticipation(dto.statusPaymentEventParticipation());
        }

        if (dto.hasParticipated() != null) {
            participation.setHasParticipated(dto.hasParticipated());
        }

        if (dto.userFunctionInEvent() != null) {
            participation.setUserFunctionInEvent(dto.userFunctionInEvent());
        }

        if (dto.amountPaid() != null) {
            participation.setAmountPaid(dto.amountPaid());

            if (dto.amountPaid() >= participation.getEvent().getValue()) {
                participation.setStatusPaymentEventParticipation(StatusPaymentEventParticipation.PAGO);

                EventTransaction transaction = new EventTransaction();
                transaction.setEvent(participation.getEvent());
                transaction.setValue(dto.amountPaid());
                transaction.setDateTransaction(new Date());
                transaction.setTransactionType(TransactionType.ENTRADA);
                transaction.setDesciption("Pagamento atualizado do Participante " +
                        participation.getUser().getName() +
                        " para o Evento " + participation.getEvent().getTitle());
                eventTransactionRepository.save(transaction);
            } else {
                participation.setStatusPaymentEventParticipation(StatusPaymentEventParticipation.PENDENTE);
            }
        }

        EventParticipation updated = eventParticipationRepository.save(participation);
        return new EventParticipationResponse().toEventParticipation(updated, null);
    }

    public EventParticipationResponse getParticipationById(UUID userId, UUID eventId) {
        EventParticipation participation = eventParticipationRepository.findByUser_IdAndEvent_Id(userId,eventId)
                .orElseThrow(() -> new EventIllegalArumentException(
                        "Participation with id " + userId + " not found."));

        return new EventParticipationResponse().toEventParticipation(participation, null);
    }

    public List<EventParticipationResponse> getAllParticipations() {
        List<EventParticipation> participations = eventParticipationRepository.findAll();

        return participations.stream()
                .map(participation -> new EventParticipationResponse(
                        participation.getId(),
                        participation.getUser().getId(),
                        participation.getEvent().getId(),
                        participation.getStatusPaymentEventParticipation(),
                        participation.getHasParticipated(),
                        participation.getUserFunctionInEvent(),
                        null
                ))
                .toList();
    }

    public List<UserInEventInfosResponse> getAllParticipationsByEventId(UUID eventId) {
        List<EventParticipation> participations = eventParticipationRepository.findAllByEvent_Id(eventId);
        List<UserInEventInfosResponse> users = new ArrayList<>();
        for (EventParticipation participation : participations) {
            users.add(new UserInEventInfosResponse().toResponse(participation, users.size()));
        }
        return users;
    }

    public List<UserInEventInfosResponse> getAllUserParticipationByUserId(UUID userId) {
        List<EventParticipation> participations = eventParticipationRepository.findAllByUser_Id(userId);
        List<UserInEventInfosResponse> users = new ArrayList<>();
        for (EventParticipation participation : participations) {
            users.add(new UserInEventInfosResponse().toResponse(participation, users.size()));
        }
        return users;
    }

    public EventParticipationResponse addFunctionUserInEvent(UUID eventParticipationID, UserFunctionInEvent function) {
        EventParticipation eventParticipation = this.eventParticipationRepository.findById(eventParticipationID)
                .orElseThrow(
                        () -> new EventIllegalArumentException(
                                "Event with id " + eventParticipationID + " does not exist.")
                );
        eventParticipation.setUserFunctionInEvent(function);
        eventParticipationRepository.save(eventParticipation);
        return new EventParticipationResponse().toEventParticipation(eventParticipation, null);
    }

    public UserEventStatus getStatusParticipationOfEvent(UUID userId, UUID eventId) {
        Optional<EventParticipation> eventParticipation = eventParticipationRepository.findByUser_IdAndEvent_Id(userId,
                eventId);
        if (eventParticipation.isEmpty()) {
            return new UserEventStatus(userId, UserEventStatusTypes.NAO_PARTICIPA,
                    StatusPaymentEventParticipation.NAO_PAGO, null);
        }
        EventParticipation participation = eventParticipation.get();
        if (participation.getPaidDate() == null) {
            return new UserEventStatus(userId, UserEventStatusTypes.PENDENTE, StatusPaymentEventParticipation.PENDENTE, null);
        }
        return new UserEventStatus(userId, UserEventStatusTypes.PARTICIPANTE, StatusPaymentEventParticipation.PAGO,
                participation.getPaidDate());
    }

    public List<UserEventStatus> getStatusPayementParticipationOfEvent(UUID eventId, StatusPaymentEventParticipation status) {
        List<EventParticipation> allByEventIdAndStatusPaymentEventParticipation =
                this.eventParticipationRepository.findAllByEvent_IdAndStatusPaymentEventParticipation(eventId, status);

        if (allByEventIdAndStatusPaymentEventParticipation.isEmpty()) {
            throw new EventIllegalArumentException("The list of status " + status + " is empty");
        }

        return allByEventIdAndStatusPaymentEventParticipation.stream()
                .map(participation -> new UserEventStatus(
                        participation.getId(),
                        UserEventStatusTypes.PARTICIPANTE,
                        participation.getStatusPaymentEventParticipation(),
                        participation.getPaidDate()
                )).collect(Collectors.toList());
    }

    public EventParticipation getUserParticipationInEventByUserId(UUID userId, UUID eventId) {
        return eventParticipationRepository.findByUser_IdAndEvent_Id(userId, eventId)
                .orElseThrow(() -> new EventIllegalArumentException(
                        "Event with id " + eventId + " or user with id " + userId + " does not exist."));
    }
}