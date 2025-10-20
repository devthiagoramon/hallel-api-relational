package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.StatusPaymentEventParticipation;
import br.hallel.relational.api.app.event.model.UserFunctionInEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventParticipationAdmDTO {
    private UUID eventId;
    private UUID userId;
    private StatusPaymentEventParticipation statusPayment;
    private UserFunctionInEvent userFunctionInEvent;
    private String community;
    private String name;
    private String email;
    private String phoneNumber;
}
