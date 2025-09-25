package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter@Setter
@AllArgsConstructor @NoArgsConstructor
public class EventDTO {

    @NotBlank(message = "Campo 'Title' não pode ser nulo.")
    private String title;

    @NotBlank(message = "Campo 'Description' não pode ser nulo.")
    private String description;

    @NotNull(message = "Campo 'date' não pode ser nulo.")
    private Date date;

    private Duration duration;

    private String local_event_name;
    private double local_event_longitude;
    private double local_event_latitude;
    private Boolean isImportant;
    private List<UUID> ministryIds;
    private String value;
    private EventType eventType;
    private List<String> schedule;
    private Boolean itsFree;

}
