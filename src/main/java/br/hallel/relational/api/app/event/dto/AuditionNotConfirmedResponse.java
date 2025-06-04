package br.hallel.relational.api.app.event.dto;

import java.util.UUID;

public record AuditionNotConfirmedResponse(
        UUID userId,
        UUID auditionId,
        String reason_abscence
) {
}
