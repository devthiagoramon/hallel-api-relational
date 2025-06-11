package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter @AllArgsConstructor
public class MemberNotConfirmedResponse {
    private UUID id;
    private User user;
    private UUID eventScaleId;
    private String reasonAbscence;
}
