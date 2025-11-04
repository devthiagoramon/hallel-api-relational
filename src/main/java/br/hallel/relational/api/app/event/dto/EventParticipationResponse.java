package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.EventParticipation;
import br.hallel.relational.api.app.event.model.enum_type.AgeGroup;
import br.hallel.relational.api.app.event.model.enum_type.StatusPaymentEventParticipation;
import br.hallel.relational.api.app.event.model.enum_type.UserFunctionInEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
public class EventParticipationResponse {

    private final UUID id;
    private final UUID userId;
    private final UUID eventId;
    private final StatusPaymentEventParticipation statusPaymentEventParticipation;
    private final String community;
    private final String name;
    private final String email;
    private final String phoneNumber;
    private final OffsetDateTime dateBirth;
    private final Boolean isMarried;
    private final Boolean hasParticipated;
    private final UserFunctionInEvent userFunctionInEvent;
    private final String qrCode;

    private final Boolean limiteIsReached;
    private final AgeGroup ageGroup;

    public EventParticipationResponse(UUID id, UUID userId, UUID eventId,
                                      StatusPaymentEventParticipation statusPaymentEventParticipation,
                                      String community, String name, String email,
                                      String phoneNumber, OffsetDateTime dateBirth,
                                      Boolean isMarried, Boolean hasParticipated,
                                      UserFunctionInEvent userFunctionInEvent, String qrCode) {

        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
        this.statusPaymentEventParticipation = statusPaymentEventParticipation;
        this.community = community;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateBirth = dateBirth;
        this.isMarried = isMarried;
        this.hasParticipated = hasParticipated;
        this.userFunctionInEvent = userFunctionInEvent;
        this.qrCode = qrCode;
        this.limiteIsReached = false;
        this.ageGroup = null;
    }

    public EventParticipationResponse(Boolean limiteIsReached, AgeGroup ageGroup, UUID eventId, UUID userId) {
        this.limiteIsReached = limiteIsReached;
        this.ageGroup = ageGroup;
        this.eventId = eventId;
        this.userId = userId;

        this.id = null;
        this.statusPaymentEventParticipation = null;
        this.community = null;
        this.name = null;
        this.email = null;
        this.phoneNumber = null;
        this.dateBirth = null;
        this.isMarried = false;
        this.hasParticipated = false;
        this.userFunctionInEvent = null;
        this.qrCode = null;
    }



    public static EventParticipationResponse toEventParticipationLimitReached(
            Boolean limiteIsReached, AgeGroup ageGroup, UUID eventId, UUID userId) {
        return new EventParticipationResponse(limiteIsReached, ageGroup, eventId, userId);
    }

    public static EventParticipationResponse toEventParticipation(EventParticipation response, String qrCode) {
        return new EventParticipationResponse(
                response.getId(),
                response.getUser() != null ? response.getUser().getId() : null,
                response.getEvent().getId(),
                response.getStatusPaymentEventParticipation(),
                response.getCommunity(),
                response.getName(),
                response.getEmail(),
                response.getPhoneNumber(),
                response.getDateBirth(),
                response.getIsMarried(),
                response.getHasParticipated(),
                response.getUserFunctionInEvent(),
                qrCode
        );
    }
}
