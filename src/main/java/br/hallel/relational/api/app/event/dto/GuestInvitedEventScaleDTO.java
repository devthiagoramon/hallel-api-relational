package br.hallel.relational.api.app.event.dto;

import java.util.UUID;

public record GuestInvitedEventScaleDTO(
        String name, String email, String phone, UUID eventScaleId, UUID inviteEventScaleId, String message
) {
}
