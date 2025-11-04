// Modifique este arquivo: br/hallel/relational/api/app/event/dto/EventInviteBatchDTO.java
package br.hallel.relational.api.app.event.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero; // Importe
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

    @NotBlank(message = "O nome do lote é obrigatório (ex: 'Lote 1')")
    private String name; // Adicionei um nome para facilitar a identificação

    @NotNull(message = "O número máximo de inscrições é obrigatório")
    @Min(value = 1, message = "O número máximo deve ser pelo menos 1")
    private int maxNumber;

    // --- NOVO CAMPO ---
    @NotNull(message = "O acréscimo de valor é obrigatório (pode ser 0)")
    @PositiveOrZero(message = "O acréscimo não pode ser negativo")
    private Double valueIncrease;
}