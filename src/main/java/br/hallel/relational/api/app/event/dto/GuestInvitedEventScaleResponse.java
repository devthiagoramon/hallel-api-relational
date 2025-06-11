package br.hallel.relational.api.app.event.dto;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class GuestInvitedEventScaleResponse {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private UUID eventScaleId;
    private UUID inviteEventScaleId;

    public GuestInvitedEventScaleResponse(UUID id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
