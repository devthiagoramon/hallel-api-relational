package br.hallel.relational.api.app.event.service;

import br.hallel.relational.api.app.event.dto.EventParticipationDTO;
import br.hallel.relational.api.app.event.dto.EventParticipationResponse;
import br.hallel.relational.api.app.event.exception.EventIllegalArumentException;
import br.hallel.relational.api.app.event.model.Event;
import br.hallel.relational.api.app.event.model.EventParticipation;
import br.hallel.relational.api.app.event.model.UserFunctionInEvent;
import br.hallel.relational.api.app.event.repository.EventParticipationRepository;
import br.hallel.relational.api.app.event.repository.EventRepository;
import br.hallel.relational.api.app.user.exceptions.UserNotFoundException;
import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserEventService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventParticipationRepository eventParticipationRepository;

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
        eventParticipation.setStatusPaymentEventParticipation(dto.statusPaymentEventParticipation());
        eventParticipation.setHasParticipated(false);

        EventParticipation participationSaved = eventParticipationRepository.save(eventParticipation);
        return new EventParticipationResponse(
                participationSaved.getId(), participationSaved.getUser(), participationSaved.getEvent(),
                participationSaved.getStatusPaymentEventParticipation(), participationSaved.getHasParticipated(),
                participationSaved.getUserFunctionInEvent()
        );
    }

    public boolean leaveTheEvent(UUID eventID) {
        Optional<EventParticipation> optional = this.eventParticipationRepository.findById(eventID);
        if (optional.isEmpty()) {
            throw new EventIllegalArumentException("Participation Event with id " + eventID + " does not exist.");
        }
        this.eventParticipationRepository.delete(optional.get());
        return true;
    }

    public EventParticipationResponse editParticipationEvent(UUID participationId, EventParticipationDTO dto) {

        EventParticipation participation = eventParticipationRepository.findById(participationId)
                .orElseThrow(() -> new EventIllegalArumentException("Participation with id " + participationId + " does not exist."));

        if (dto.statusPaymentEventParticipation() != null) {
            participation.setStatusPaymentEventParticipation(dto.statusPaymentEventParticipation());
        }

        if (dto.hasParticipated() != null) {
            participation.setHasParticipated(dto.hasParticipated());
        }

        if (dto.userFunctionInEvent() != null) {
            participation.setUserFunctionInEvent(dto.userFunctionInEvent());
        }

        EventParticipation updated = eventParticipationRepository.save(participation);

        return new EventParticipationResponse(
                updated.getId(),
                updated.getUser(),
                updated.getEvent(),
                updated.getStatusPaymentEventParticipation(),
                updated.getHasParticipated(),
                updated.getUserFunctionInEvent()
        );
    }

    public EventParticipationResponse getParticipationById(UUID participationId) {
        EventParticipation participation = eventParticipationRepository.findById(participationId)
                .orElseThrow(() -> new EventIllegalArumentException("Participation with id " + participationId + " not found."));

        return new EventParticipationResponse(
                participation.getId(),
                participation.getUser(),
                participation.getEvent(),
                participation.getStatusPaymentEventParticipation(),
                participation.getHasParticipated(),
                participation.getUserFunctionInEvent()
        );
    }

    public List<EventParticipationResponse> getAllParticipations() {
        List<EventParticipation> participations = eventParticipationRepository.findAll();

        return participations.stream()
                .map(participation -> new EventParticipationResponse(
                        participation.getId(),
                        participation.getUser(),
                        participation.getEvent(),
                        participation.getStatusPaymentEventParticipation(),
                        participation.getHasParticipated(),
                        participation.getUserFunctionInEvent()
                ))
                .toList();
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