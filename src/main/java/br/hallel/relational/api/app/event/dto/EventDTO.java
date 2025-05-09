package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.Event;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public record EventDTO(
        @NotBlank(message = "Campo 'Title' não pode ser nulo.")
        String title,

        @NotBlank(message = "Campo 'Description' não pode ser nulo.")
        String description,

        @NotBlank(message = "Campo 'Img_Url' não pode ser nulo.")
        String image_url,

        @NotBlank(message = "Campo 'Banner_Url' não pode ser nulo.")
        String banner_url,

        @NotNull(message = "Campo 'date' não pode ser nulo.")
        Date date,

        String local_event_name,
        double local_event_longitude,
        double local_event_latitude,
        Boolean isImportant,
        @NotNull(message = "Campo 'date_hours' não pode ser nulo." )
        String date_hours
) {
}
