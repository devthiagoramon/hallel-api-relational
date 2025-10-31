package br.hallel.relational.api.app.event.dto;


import br.hallel.relational.api.app.event.model.StatusPaymentEventParticipation;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserEventStatus(UUID userId,
                              UserEventStatusTypes status,
                              StatusPaymentEventParticipation paymentStatus,
                              OffsetDateTime paidDate) {
}
