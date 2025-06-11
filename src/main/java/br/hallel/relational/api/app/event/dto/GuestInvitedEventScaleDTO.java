package br.hallel.relational.api.app.event.dto;

import java.util.UUID;

public record GuestInvitedEventScaleDTO(
        String name, String email, UUID inviteEventScaleId
) {
}
