package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.EventParticipation;
import br.hallel.relational.api.app.event.model.StatusPaymentEventParticipation;
import br.hallel.relational.api.app.event.model.UserFunctionInEvent;
import lombok.*;

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

    private Boolean hasParticipated;

    private UserFunctionInEvent userFunctionInEvent;

    private String qrCode;

    public static EventParticipationResponse toEventParticipation(EventParticipation response, String qrCode) {
        return EventParticipationResponse.builder()
                .id(response.getId())
                .userId(response.getUser().getId())
                .eventId(response.getEvent().getId())
                .statusPaymentEventParticipation(response.getStatusPaymentEventParticipation())
                .community(response.getCommunity())
                .name(response.getName())
                .email(response.getEmail())
                .phoneNumber(response.getPhoneNumber())
                .hasParticipated(response.getHasParticipated())
                .userFunctionInEvent(response.getUserFunctionInEvent())
                .qrCode(qrCode)
                .build();
    }
}