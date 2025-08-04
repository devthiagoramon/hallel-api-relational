package br.hallel.relational.api.app.ministry.dto;

import br.hallel.relational.api.app.user.model.User;

import java.util.UUID;

public record ScaleChatParticipantUserResponse(UUID scaleMemberChatId, User user) {
}
