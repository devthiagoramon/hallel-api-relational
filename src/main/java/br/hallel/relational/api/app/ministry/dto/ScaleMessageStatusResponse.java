package br.hallel.relational.api.app.ministry.dto;

import br.hallel.relational.api.app.user.model.User;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ScaleMessageStatusResponse(UUID id, UUID chatParticipantId, User user, OffsetDateTime updatedAt) {
}
