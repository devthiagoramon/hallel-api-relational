package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.enum_type.AgeGroup;
import br.hallel.relational.api.app.event.model.EventParticipation;
import br.hallel.relational.api.app.event.model.enum_type.StatusPaymentEventParticipation;
import br.hallel.relational.api.app.event.model.enum_type.UserFunctionInEvent;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
public class EventParticipationResponse {

    private UUID id;

    private UUID userId;

    private UUID eventId;

    private StatusPaymentEventParticipation statusPaymentEventParticipation;

    private String community;

    private String name;

    private String email;

    private String phoneNumber;

    private OffsetDateTime dateBirth;

    private Boolean isMarried;

    private Boolean hasParticipated;

    private UserFunctionInEvent userFunctionInEvent;

    private String qrCode;

    private Boolean limiteIsReached;

    private AgeGroup ageGroup;

    public EventParticipationResponse(Boolean limiteIsReached, AgeGroup ageGroup, UUID eventId, UUID userId) {
        this.limiteIsReached = limiteIsReached;
        this.ageGroup = ageGroup;
        this.eventId = eventId;
        this.userId = userId;
    }

    public static EventParticipationResponse toEventParticipationLimitReached(
            Boolean limiteIsReached, AgeGroup ageGroup, UUID eventId, UUID userId) {
        return new EventParticipationResponse(limiteIsReached, ageGroup, eventId, userId);
    }
    public static EventParticipationResponse toEventParticipation(EventParticipation response, String qrCode) {
        return EventParticipationResponse.builder()
                .id(response.getId())
                .userId(response.getUser() != null ? response.getUser().getId() : null)
                .eventId(response.getEvent().getId())
                .statusPaymentEventParticipation(response.getStatusPaymentEventParticipation())
                .community(response.getCommunity())
                .name(response.getName())
                .email(response.getEmail())
                .phoneNumber(response.getPhoneNumber())
                .dateBirth(response.getDateBirth())
                .isMarried(response.getIsMarried())
                .hasParticipated(response.getHasParticipated())
                .userFunctionInEvent(response.getUserFunctionInEvent())
                .qrCode(qrCode)
                .build();
    }
}