package br.hallel.relational.api.app.event.dto;

import java.time.LocalDateTime;

public record EventScaleMemberCanParticipateDTO(LocalDateTime start, LocalDateTime end) {
}
