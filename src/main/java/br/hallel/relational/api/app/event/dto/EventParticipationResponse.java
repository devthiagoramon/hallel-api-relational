package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.EventParticipation;
import br.hallel.relational.api.app.event.model.StatusPaymentEventParticipation;
import br.hallel.relational.api.app.event.model.UserFunctionInEvent;
import lombok.*;

import java.util.UUID;

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

    private Boolean hasParticipated;

    private UserFunctionInEvent userFunctionInEvent;

    public EventParticipationResponse toEventParticipation(EventParticipation response) {

        return new EventParticipationResponse(response.getId(), response.getUser().getId(), response.getEvent().getId(),
                response.getStatusPaymentEventParticipation(), response.getHasParticipated(),
                response.getUserFunctionInEvent());
    }
}