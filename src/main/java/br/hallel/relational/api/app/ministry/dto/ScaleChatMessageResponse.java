package br.hallel.relational.api.app.ministry.dto;

import br.hallel.relational.api.app.ministry.model.MessageScaleDeliveryStatus;
import br.hallel.relational.api.app.ministry.model.ScaleChatMessageVisibility;
import br.hallel.relational.api.app.ministry.model.ScaleMessageType;
import br.hallel.relational.api.app.user.model.User;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ScaleChatMessageResponse(UUID id, UUID scaleId, UUID participantSenderId, User userSender, String content,
                                       ScaleMessageType contentType, OffsetDateTime sentAt, OffsetDateTime updatedAt,
                                       MessageScaleDeliveryStatus statusMessage, ScaleChatMessageVisibility visibility) {
}
