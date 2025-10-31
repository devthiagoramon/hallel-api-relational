package br.hallel.relational.api.app.email.dto;

import java.time.LocalDateTime;

public record EmailParticipationDTO(

        String to, String name,
        LocalDateTime eventDate,
        String eventTitle
) {
}
