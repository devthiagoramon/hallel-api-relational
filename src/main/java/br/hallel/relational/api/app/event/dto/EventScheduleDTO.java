// Verifique este arquivo: br/hallel/relational/api/app/event/dto/EventScheduleDTO.java
package br.hallel.relational.api.app.event.dto;


import br.hallel.relational.api.app.event.model.enum_type.EventScheduleType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class EventScheduleDTO {

    private UUID id;

    @NotNull(message = "Campo 'description' não pode ser nulo!")
    @NotBlank(message = "Digite a descrição da atividade")
    private String description;

    @NotNull(message = "Indique a data/horário que ocorrerá a atividade!")
    @Future(message = "A data deve está no futuro!")
    private OffsetDateTime date;

    @NotNull(message = "O tipo da atividade não pode ser nulo")
    private EventScheduleType type;

    private List<UUID> ministryIds = new ArrayList<>();

    private List<UUID> userIds = new ArrayList<>();

    @Valid
    private List<EventScheduleVisitorDTO> visitors = new ArrayList<>();
}