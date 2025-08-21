package br.hallel.relational.api.app.event.service;

import br.hallel.relational.api.app.event.dto.EventParticipationDTO;
import br.hallel.relational.api.app.event.dto.EventParticipationResponse;
import br.hallel.relational.api.app.event.dto.UserInEventInfosResponse;
import br.hallel.relational.api.app.event.exception.EventIllegalArumentException;
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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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

    public EventParticipationResponse joinTheEvent(EventParticipationDTO dto) {
        Event event = this.eventRepository.findById(dto.eventID()).orElseThrow(
                () -> new EventIllegalArumentException("Event with id " + dto.eventID() + " does not exist.")
        );
        User user = this.userRepository.findById(dto.userID()).orElseThrow(
                () -> new UserNotFoundException("User with id " + dto.userID() + " does not exist.")
        );

        boolean alreadyParticipating = eventParticipationRepository.existsByUserAndEvent(user, event);
        if (alreadyParticipating) {
            log.warn("Usuário ID {} já está participando do evento ID {}. Lançando exceção.", dto.userID(), dto.eventID());
            throw new EventIllegalArumentException("User already participating in this event.");
        }

        EventParticipation eventParticipation = new EventParticipation();
        eventParticipation.setUser(user);
        eventParticipation.setEvent(event);
        eventParticipation.setUserFunctionInEvent(dto.userFunctionInEvent());
        eventParticipation.setHasParticipated(false);
        eventParticipation.setAmountPaid(dto.amountPaid());

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
                CreatePixPaymentRequestDTO paymentRequestDTO = new CreatePixPaymentRequestDTO(
                        BigDecimal.valueOf(event.getValue()),
                        event.getTitle(),
                        user.getEmail(),
                        firstName,
                        lastName,
                        user.getCpf()
                );

                Payment payment = mercadoPagoClient.createPixPayment(paymentRequestDTO);

                // Verificação de segurança adicional para evitar NPE
                if (payment != null && payment.getPointOfInteraction() != null &&
                        payment.getPointOfInteraction().getTransactionData() != null) {
                    eventParticipation.setStatusPaymentEventParticipation(StatusPaymentEventParticipation.PENDENTE);
                    eventParticipation.setPixTxid(payment.getPointOfInteraction().getTransactionData().getQrCode());
                    qrCodeBase64 = payment.getPointOfInteraction().getTransactionData().getQrCodeBase64();

                    log.info("Pagamento Pix criado com sucesso para o usuário ID {}. TXID: {}", dto.userID(), eventParticipation.getPixTxid());
                } else {
                    log.error("Resposta do Mercado Pago incompleta, dados de transação ou de interação nulos.");
                    throw new RuntimeException("Erro ao processar a resposta do Mercado Pago.");
                }

            } catch (MPException | MPApiException e) {
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

    public EventParticipationResponse getParticipationById(UUID participationId) {
        EventParticipation participation = eventParticipationRepository.findById(participationId)
                .orElseThrow(() -> new EventIllegalArumentException("Participation with id " + participationId + " not found."));

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
        EventParticipation eventParticipation = this.eventParticipationRepository.findById(eventParticipationID).orElseThrow(
                () -> new EventIllegalArumentException("Event with id " + eventParticipationID + " does not exist.")
        );
        eventParticipation.setUserFunctionInEvent(function);
        eventParticipationRepository.save(eventParticipation);
        return new EventParticipationResponse().toEventParticipation(eventParticipation, null);
    }

}