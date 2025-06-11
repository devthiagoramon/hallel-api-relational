package br.hallel.relational.api.app.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Getter
@AllArgsConstructor @NoArgsConstructor
public class MemberInvitedAndConfirmedResponse {
    private UUID id;
    private String name;
    private String email;
    private UUID eventScaleId;
}
