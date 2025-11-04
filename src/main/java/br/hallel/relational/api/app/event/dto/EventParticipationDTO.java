package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.enum_type.StatusPaymentEventParticipation;
import br.hallel.relational.api.app.event.model.enum_type.UserFunctionInEvent;

import java.util.UUID;

public record EventParticipationDTO(
        UUID userID,
        UUID eventID,
        StatusPaymentEventParticipation statusPaymentEventParticipation,
        String community,
        Boolean hasParticipated,
        UserFunctionInEvent userFunctionInEvent,
        Double amountPaid) {
}