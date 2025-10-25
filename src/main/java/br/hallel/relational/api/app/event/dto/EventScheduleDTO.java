package br.hallel.relational.api.app.event.dto;


import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class EventScheduleDTO {

    private UUID id;

    @NotNull(message = "Indique qual evento a atividade pertence!")
    private UUID event_id;

    @NotNull(message = "Campo 'description' não pode ser nulo!")
    @NotBlank(message = "Digite a descrição da atividade")
    private String description;

    @NotNull(message = "Indique a data/horário que ocorrerá a atividade!")
    @Future(message = "A data deve está no futuro!")
    private OffsetDateTime date;

    private UUID ministryId;

}
