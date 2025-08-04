package br.hallel.relational.api.app.ministry.dto;

import br.hallel.relational.api.app.event.dto.EventShortResponse;
import br.hallel.relational.api.app.user.model.User;

import java.util.List;
import java.util.UUID;

public record ScaleChatInfoResponse(UUID scaleId, EventShortResponse eventScale, List<User> participants) {
}
