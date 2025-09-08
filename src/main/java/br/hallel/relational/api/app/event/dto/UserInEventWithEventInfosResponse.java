package br.hallel.relational.api.app.event.dto;


import br.hallel.relational.api.app.event.model.Event;
import br.hallel.relational.api.app.event.model.EventParticipation;
import br.hallel.relational.api.app.event.model.StatusPaymentEventParticipation;
import br.hallel.relational.api.app.event.model.UserFunctionInEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserInEventWithEventInfosResponse {
    private UUID participationId;
    private EventShortResponse event;
    private UserFunctionInEvent userFunctionInEvent;
    private StatusPaymentEventParticipation statusPaymentEventParticipation;

    public UserInEventWithEventInfosResponse toResponse(EventParticipation eventParticipation) {
        Event eventToResponse = eventParticipation.getEvent();
        return new UserInEventWithEventInfosResponse(eventParticipation.getId(),
                new EventShortResponse(eventToResponse.getId(), eventToResponse.getTitle(), eventToResponse.getDate(),
                        eventToResponse.getImage_url(), eventToResponse.getBanner_url(), eventToResponse.getItsFree(), eventToResponse.getEventType()),
                eventParticipation.getUserFunctionInEvent(), eventParticipation.getStatusPaymentEventParticipation());
    }
}
