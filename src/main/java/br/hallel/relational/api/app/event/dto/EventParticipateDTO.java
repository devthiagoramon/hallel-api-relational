package br.hallel.relational.api.app.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventParticipateDTO {
    private UUID eventId;
    private String community;
    private String name;
    private String email;
    private String phoneNumber;
    private String cpf;
    private Boolean isMarried;
    private OffsetDateTime dateBirth;
}
