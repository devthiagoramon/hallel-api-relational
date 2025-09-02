package br.hallel.relational.api.app.event.service;

import br.hallel.relational.api.app.event.dto.*;
import br.hallel.relational.api.app.event.exception.*;
import br.hallel.relational.api.app.event.model.*;
import br.hallel.relational.api.app.event.repository.EventParticipationRepository;
import br.hallel.relational.api.app.event.repository.EventRepository;
import br.hallel.relational.api.app.event.repository.EventTransactionRepository;
import br.hallel.relational.api.app.payment.checkout_transparent.client.MercadoPagoClient;
import br.hallel.relational.api.app.payment.checkout_transparent.dto.CreatePixPaymentRequestDTO;
import br.hallel.relational.api.app.user.exceptions.UserNotFoundException;
import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.repository.UserRepository;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    private final SimpMessagingTemplate template;

    public EventParticipationResponse joinTheEvent(UUID userId, EventParticipateDTO dto) {
        Event event = this.eventRepository.findById(dto.getEventId()).orElseThrow(
                () -> new EventNotFoundException("event.id.not.found", dto.getEventId().toString())
        );
        User user = this.userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("user.not.found", userId.toString())
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
        eventParticipation.setCommunity(dto.getCommunity());

        String qrCodeBase64 = null;

        if (!event.getItsFree() || event.getValue() > 0) {
            try {
                String fullName = user.getName();
                String firstName = "";
                String lastName = "";

                if (fullName != null && !fullName.isEmpty()) {
                    String[] names = fullName.split(" ");
                    if (names.length > 0) {
                        firstName = names[0];
                    }
                    if (names.length > 1) {
                        lastName = String.join(" ", java.util.Arrays.copyOfRange(names, 1, names.length));
                    }
                }

                if (user.getCpf() == null || user.getCpf().isEmpty()) {
                    throw new UserValidationException("User CPF is required to make the payment.");

                }

                CreatePixPaymentRequestDTO paymentRequestDTO = new CreatePixPaymentRequestDTO(
                        BigDecimal.valueOf(event.getValue()),
                        event.getTitle(),
                        user.getEmail(),
                        firstName,
                        lastName,
                        user.getCpf()
                );

                Payment payment = mercadoPagoClient.createPixPayment(paymentRequestDTO, userId);

                // Verificação de segurança adicional para evitar NPE
                if (payment != null && payment.getPointOfInteraction() != null &&
                        payment.getPointOfInteraction().getTransactionData() != null) {
                    eventParticipation.setStatusPaymentEventParticipation(StatusPaymentEventParticipation.PENDENTE);
                    eventParticipation.setPixTxid(payment.getPointOfInteraction().getTransactionData().getQrCode());
                    eventParticipation.setMercadoPagoPaymentId(payment.getId());
                    qrCodeBase64 = payment.getPointOfInteraction().getTransactionData().getQrCodeBase64();
                    String linkCodePayment = payment.getPointOfInteraction().getTransactionData().getQrCode();


                    template.convertAndSend("/topic/payments/" + user.getId(),
                            new PaymentStatusDTO(qrCodeBase64, linkCodePayment,
                                    StatusPaymentEventParticipation.PENDENTE));


                    log.info("Pagamento Pix criado com sucesso para o usuário ID {}. TXID: {}", userId,
                            eventParticipation.getPixTxid());
                } else {
                    log.error("Resposta do Mercado Pago incompleta, dados de transação ou de interação nulos.");
                    throw new RuntimeException("Erro ao processar a resposta do Mercado Pago.");
                }

            } catch (MPApiException apiException) {
                log.error("Erro na API do Mercado Pago. Status: {}, Mensagem: {}. Detalhes: {}",
                        apiException.getStatusCode(),
                        apiException.getMessage(),
                        apiException.getApiResponse());
                throw new RuntimeException("Erro ao criar pagamento Pix. Por favor, tente novamente.", apiException);
            } catch (MPException | RuntimeException e) {
                log.error("Erro ao criar pagamento Pix no Mercado Pago: {}", e.getMessage(), e);
                throw new RuntimeException("Erro ao criar pagamento Pix. Por favor, tente novamente.", e);
            }
        } else {
            eventParticipation.setStatusPaymentEventParticipation(StatusPaymentEventParticipation.PAGO);
        }

        EventParticipation participationSaved = eventParticipationRepository.save(eventParticipation);
        log.info("Participação do evento salva no banco de dados com ID: {}", participationSaved.getId());
        return new EventParticipationResponse().toEventParticipation(participationSaved, qrCodeBase64);

    }

    public EventPayParticipationDetails payAnEvent(UUID userId, UUID eventId) {
        Event event = this.eventRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException("event.id.not.found", eventId.toString())
        );

        User user = this.userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("user.not.found", userId.toString())
        );

        boolean alreadyParticipating = eventParticipationRepository.existsByUserAndEvent(user, event);
        if (!alreadyParticipating) {
            log.warn("Usuário ID {} não está participando do evento ID {}. Lançando exceção.", userId,
                    eventId);
            throw new EventParticipationException("participation.event.not.found");
        }

        EventParticipation participation = eventParticipationRepository.findByUser_IdAndEvent_Id(userId, eventId).get();

        if (participation.getPixTxid() == null) {
            throw new EventParticipationException("This user no have an pix to pay");
        }

        if (participation.getStatusPaymentEventParticipation() == StatusPaymentEventParticipation.PAGO) {
            throw new EventParticipationException("This user already pay the event.");
        }

        byte[] pixTxidBytes = participation.getPixTxid().getBytes();

        return new EventPayParticipationDetails(Base64.getEncoder().encodeToString(pixTxidBytes),
                participation.getPixTxid(),
                event.getValue());
    }


    public boolean leaveTheEvent(UUID eventId, UUID userId) {
        this.eventRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException("event.id.not.found", eventId.toString())
        );
        this.userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("user.not.found", userId.toString())
        );


        EventParticipation participation = eventParticipationRepository.findByUser_IdAndEvent_Id(userId, eventId)
                .orElseThrow(() -> new EventParticipationException(
                        "participation.event.not.found"
                ));


        if (participation.getStatusPaymentEventParticipation() == StatusPaymentEventParticipation.PAGO) {
            log.info(
                    "Participação do evento está paga. Tentando solicitar o reembolso para o pagamento de Mercado Pago ID: {}",
                    participation.getMercadoPagoPaymentId());
            try {

                this.mercadoPagoClient.requestRefund(
                        participation.getMercadoPagoPaymentId(),
                        participation.getAmountPaid()
                );
            } catch (PaymentRefundException e) {

                log.error("Falha ao solicitar o reembolso para o pagamento {} do evento {}. Motivo: {}",
                        participation.getMercadoPagoPaymentId(), eventId, e.getMessage());
                throw new EventIllegalArumentException(
                        "Não foi possível solicitar o reembolso. A transação foi cancelada.");
            }
        }

        if (participation.getMercadoPagoPaymentId() != null) {
            eventTransactionRepository.findByMercadoPagoPaymentId(participation.getMercadoPagoPaymentId())
                    .ifPresent(eventTransactionRepository::delete);
            log.info("Transação do evento para o pagamento {} deletada com sucesso.",
                    participation.getMercadoPagoPaymentId());
        }

        eventParticipationRepository.delete(participation);
        log.info("Participação do usuário {} no evento {} deletada com sucesso.", userId, eventId);

        return true;
    }

    public EventParticipationResponse editParticipationEvent(UUID userId, EventParticipationDTO dto) {

        eventRepository.findById(dto.eventID()).orElseThrow(
                () -> new EventNotFoundException("event.id.not.found", dto.eventID().toString())
        );
        userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("user.not.found", userId.toString())
        );

        EventParticipation participation = eventParticipationRepository.findByUser_IdAndEvent_Id(userId, dto.eventID())
                .orElseThrow(() -> new EventParticipationException(
                        "participation.event.not.found"
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

        if (dto.community() != null) {
            participation.setCommunity(dto.community());
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
        EventParticipation participation = eventParticipationRepository.findByUser_IdAndEvent_Id(userId, eventId)
                .orElseThrow(() -> new EventParticipationException(
                        "participation.event.not.found"));

        return new EventParticipationResponse().toEventParticipation(participation, null);
    }

    public Page<EventParticipationResponse> getAllParticipations(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EventParticipation> participations = eventParticipationRepository.findAll(pageable);

        return participations.map(participation -> new EventParticipationResponse(
                participation.getId(),
                participation.getUser().getId(),
                participation.getEvent().getId(),
                participation.getStatusPaymentEventParticipation(),
                participation.getCommunity(),
                participation.getHasParticipated(),
                participation.getUserFunctionInEvent(),
                null
        ));
    }

    public Page<UserInEventInfosResponse> getAllParticipationsByEventId(UUID eventId, Pageable pageable) {
        Page<EventParticipation> participations = eventParticipationRepository.findAllByEvent_Id(eventId, pageable);
        List<UserInEventInfosResponse> users = new ArrayList<>();
        for (EventParticipation participation : participations) {
            users.add(new UserInEventInfosResponse().toResponse(participation, users.size()));
        }
        return participations.map(part -> new UserInEventInfosResponse().toResponse(part, users.size()));
    }

    public Page<UserInEventWithEventInfosResponse> getAllUserParticipationByUserId(UUID userId, Pageable pageable) {
        Page<EventParticipation> participations = eventParticipationRepository.findAllByUser_Id(userId, pageable);

        return participations.map(p -> new UserInEventWithEventInfosResponse().toResponse(p));
    }

    public EventParticipationResponse addFunctionUserInEvent(UUID userId, UUID eventId, UserFunctionInEvent function) {
        EventParticipation eventParticipation = this.eventParticipationRepository.findByUser_IdAndEvent_Id(userId,
                        eventId)
                .orElseThrow(
                        () -> new EventParticipationException(
                                "event.id.not.found")
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
            return new UserEventStatus(userId, UserEventStatusTypes.PENDENTE, StatusPaymentEventParticipation.PENDENTE,
                    null);
        }
        return new UserEventStatus(userId, UserEventStatusTypes.PARTICIPANTE, StatusPaymentEventParticipation.PAGO,
                participation.getPaidDate());
    }

    public Page<UserEventStatus> getStatusPayementParticipationOfEvent(UUID eventId,
                                                                       StatusPaymentEventParticipation status,
                                                                       Pageable pageable) {
        Page<EventParticipation> allByEventIdAndStatusPaymentEventParticipation =
                this.eventParticipationRepository.findAllByEvent_IdAndStatusPaymentEventParticipation(eventId, status
                        , pageable);

        if (allByEventIdAndStatusPaymentEventParticipation.isEmpty()) {
            throw new EventIllegalArumentException("The list of status " + status + " is empty");
        }

        return allByEventIdAndStatusPaymentEventParticipation.map(participation -> new UserEventStatus(
                participation.getId(),
                UserEventStatusTypes.PARTICIPANTE,
                participation.getStatusPaymentEventParticipation(),
                participation.getPaidDate()
        ));
    }

    public EventParticipation getUserParticipationInEventByUserId(UUID userId, UUID eventId) {
        return eventParticipationRepository.findByUser_IdAndEvent_Id(userId, eventId)
                .orElseThrow(() -> new EventParticipationException("participation.event.not.found"));
    }

    public EventParticipationResponse addParticipateAsAdminService(EventParticipationAdmDTO dto) {
        EventParticipation eventParticipation = new EventParticipation();
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("user.not.found", dto.getUserId().toString()));
        Event event = eventRepository.findById(dto.getEventId()).orElseThrow(
                () -> new EventNotFoundException("event.id.not.found",
                        dto.getEventId().toString()));
        eventParticipation.setEvent(event);
        eventParticipation.setUserFunctionInEvent(dto.getUserFunctionInEvent());
        eventParticipation.setStatusPaymentEventParticipation(dto.getStatusPayment());
        eventParticipation.setUser(user);
        eventParticipation.setHasParticipated(dto.getStatusPayment() == StatusPaymentEventParticipation.PAGO);
        eventParticipation.setPaidDate(dto.getStatusPayment() == StatusPaymentEventParticipation.PAGO ? Instant.now()
                .atOffset(ZoneOffset.UTC) : null);
        return EventParticipationResponse.toEventParticipation(eventParticipationRepository.save(eventParticipation),
                null);
    }

    public UserPaymentDetailResponse getUserPaymentDetail(UUID userId, UUID eventId) {
        Event event = this.eventRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException("event.id.not.found", eventId.toString())
        );
        this.userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("user.not.found", userId.toString())
        );

        EventParticipation participation = this.eventParticipationRepository.findByUser_IdAndEvent_Id(userId, eventId)
                .orElseThrow(() -> new EventParticipationException("participation.event.not.found"));
        double valuePaid = 0;
        String comprovant = "";

        if (participation.getStatusPaymentEventParticipation() == StatusPaymentEventParticipation.PAGO
                && !event.getItsFree()) {
            valuePaid = participation.getAmountPaid();

            // Busca a transação associada para pegar o comprovante
            if (participation.getMercadoPagoPaymentId() != null) {
                Optional<EventTransaction> transaction =
                        eventTransactionRepository.findByMercadoPagoPaymentId(participation.getMercadoPagoPaymentId());

                if (transaction.isPresent()) {
                    comprovant = transaction.get().getReceiptPaymentFileImage();
                }
            }
        }


        return new UserPaymentDetailResponse(eventId, userId, valuePaid, participation.getPaidDate(),
                participation.getStatusPaymentEventParticipation(), comprovant);
    }
}