package br.hallel.relational.api.app.ministry.dto;

import java.util.UUID;

public record ScaleMessageUpdateEvent(UUID messageId, ScaleMessageUpdateEventTypes type, Object payload) {
}
