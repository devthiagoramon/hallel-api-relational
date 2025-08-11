package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.Event;
import br.hallel.relational.api.app.event.model.StatusPaymentEventParticipation;
import br.hallel.relational.api.app.event.model.UserFunctionInEvent;
import br.hallel.relational.api.app.user.model.User;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor @Getter
@Setter
@ToString
@NoArgsConstructor
public class EventParticipationResponse {

    private UUID id;

    private User user;

    private Event event;

    private StatusPaymentEventParticipation statusPaymentEventParticipation;

    private Boolean hasParticipated;

    private UserFunctionInEvent userFunctionInEvent;
}
