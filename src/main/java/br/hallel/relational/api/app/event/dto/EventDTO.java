package br.hallel.relational.api.app.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public record EventDTO(
        @NotBlank(message = "Campo 'Title' não pode ser nulo.")
        String title,

        @NotBlank(message = "Campo 'Description' não pode ser nulo.")
        String description,

        @NotNull(message = "Campo 'date' não pode ser nulo.")
        Date date,
        String local_event_name,
        double local_event_longitude,
        double local_event_latitude,
        Boolean isImportant,
        List<UUID> ministryIds
) {
}
