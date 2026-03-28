package br.hallel.relational.api.app.event.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventInviteBatchDTO {

    private UUID id;

    @NotNull(message = "O número máximo de inscrições é obrigatório")
    @Min(value = 1, message = "O número máximo deve ser pelo menos 1")
    private int maxNumber;

    @NotNull(message = "O acréscimo de valor é obrigatório (pode ser 0)")
    @PositiveOrZero(message = "O acréscimo não pode ser negativo")
    private Double valueIncrease;
}