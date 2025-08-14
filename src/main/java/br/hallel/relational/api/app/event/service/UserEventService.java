package br.hallel.relational.api.app.event.service;

import br.hallel.relational.api.app.event.dto.EventParticipationDTO;
import br.hallel.relational.api.app.event.dto.EventParticipationResponse;
import br.hallel.relational.api.app.event.dto.UserInEventInfosResponse;
import br.hallel.relational.api.app.event.exception.EventIllegalArumentException;
import br.hallel.relational.api.app.event.model.*;
import br.hallel.relational.api.app.event.repository.EventParticipationRepository;
import br.hallel.relational.api.app.event.repository.EventRepository;
import br.hallel.relational.api.app.event.repository.EventTransactionRepository;
import br.hallel.relational.api.app.user.exceptions.UserNotFoundException;
import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserEventService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventParticipationRepository eventParticipationRepository;
    private final EventTransactionRepository eventTransactionRepository;

    public EventParticipationResponse joinTheEvent(EventParticipationDTO dto) {

        Event event = this.eventRepository.findById(dto.eventID()).orElseThrow(
                () -> new EventIllegalArumentException("Event with id " + dto.eventID() + " does not exist.")
        );
        User user = this.userRepository.findById(dto.userID()).orElseThrow(
                () -> new UserNotFoundException("User with id " + dto.userID() + " does not exist.")
        );

        boolean alreadyParticipating = eventParticipationRepository.existsByUserAndEvent(user, event);
        if (alreadyParticipating) {
            throw new EventIllegalArumentException("User already participating in this event.");
        }

        EventParticipation eventParticipation = new EventParticipation();
        eventParticipation.setUser(user);
        eventParticipation.setEvent(event);
        eventParticipation.setUserFunctionInEvent(dto.userFunctionInEvent());
        eventParticipation.setHasParticipated(false);
        eventParticipation.setAmountPaid(dto.amountPaid());

        EventTransaction transaction = null;
        if (!event.getItsFree()) {
            if (eventParticipation.getAmountPaid() != null && eventParticipation.getAmountPaid() >= event.getValue()) {
                eventParticipation.setAmountPaid(event.getValue());
                eventParticipation.setStatusPaymentEventParticipation(StatusPaymentEventParticipation.PAGO);

                transaction = new EventTransaction();
                transaction.setEvent(event);
                transaction.setValue(eventParticipation.getAmountPaid());
                transaction.setDateTransaction(new Date());
                transaction.setTransactionType(TransactionType.ENTRADA);
                transaction.setDesciption("Pagamento do Participante " +
                        user.getName() + " para o Evento " + event.getTitle());
            } else {
                eventParticipation.setStatusPaymentEventParticipation(StatusPaymentEventParticipation.PENDENTE);
            }
        } else {
            eventParticipation.setStatusPaymentEventParticipation(StatusPaymentEventParticipation.PAGO);
        }

        EventParticipation participationSaved = eventParticipationRepository.save(eventParticipation);
        if (transaction != null) {
            eventTransactionRepository.save(transaction);
        }


        return new EventParticipationResponse().toEventParticipation(participationSaved);
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
        return new EventParticipationResponse().toEventParticipation(updated);
    }

    public EventParticipationResponse getParticipationById(UUID participationId) {
        EventParticipation participation = eventParticipationRepository.findById(participationId)
                .orElseThrow(() -> new EventIllegalArumentException("Participation with id " + participationId + " not found."));

        return new EventParticipationResponse().toEventParticipation(participation);
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
                        participation.getUserFunctionInEvent()
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
        return new EventParticipationResponse().toEventParticipation(eventParticipation);
    }

}