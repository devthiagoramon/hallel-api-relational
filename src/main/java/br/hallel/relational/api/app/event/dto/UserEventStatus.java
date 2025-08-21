package br.hallel.relational.api.app.event.dto;


import java.time.OffsetDateTime;
import java.util.UUID;

public record UserEventStatus(UUID userId, UserEventStatusTypes status, OffsetDateTime paidDate) {
}
