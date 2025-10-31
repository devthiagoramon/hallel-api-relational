package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.enum_type.EventType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventDTO {

    @NotBlank(message = "Campo 'Title' não pode ser nulo.")
    private String title;

    @NotBlank(message = "Campo 'Description' não pode ser nulo.")
    private String description;

    @NotNull(message = "Campo 'date' não pode ser nulo.")
    @Future(message = "A data do evento deve está no futuro!")
    private Date date;

    private Duration duration;

    @NotBlank(message = "Nome do local do evento não pode ser nulo")
    private String local_event_name;

    @NotNull(message = "Longitude do local do evento não pode ser nula.")

    private Double local_event_longitude;
    @NotNull(message = "Latitude do local do evento não pode ser nula.")
    private Double local_event_latitude;

    @Valid
    private List<EventInviteDTO> eventInviteDTOS;
    @Valid
    private List<EventScheduleDTO> eventScheduleDTOS;

    private Boolean isImportant;
    private List<UUID> ministryIds;

    @NotNull(message = "O tipo do evento não pode ser nulo")
    private EventType eventType;

    private Boolean itsFree;

    private String whatsAppGroupLink;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

}
