package br.hallel.relational.api.app.event.dto;

import java.util.Date;

public record InviteEventScaleDTO(
        String message,
        boolean isSent,
        Date dateEdit,
        Date dateSend) {
}
