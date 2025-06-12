package br.hallel.relational.api.app.event.dto;

import java.util.Date;
import java.util.UUID;

public record InviteEventScaleResponse(
        UUID id,
        boolean isSent,
        String message,
        Date dateSend,
        Date dateEdit
) {
}
