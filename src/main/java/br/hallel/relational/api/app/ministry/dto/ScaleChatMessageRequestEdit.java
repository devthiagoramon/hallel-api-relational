package br.hallel.relational.api.app.ministry.dto;

import br.hallel.relational.api.app.ministry.model.ScaleMessageType;

import java.util.UUID;

public record ScaleChatMessageRequestEdit(UUID messageId, String content) {
}
