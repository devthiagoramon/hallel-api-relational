package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.EventInvite;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class EventInviteDTO {

    private UUID id;

    @NotNull(message = "Digite o nome do convite!")
    @NotBlank(message = "Nome do convite inválido!")
    private String name;

    @NotNull(message = "Digite a descrição do convite!")
    @NotBlank(message = "Descrição do convite inválido!")
    private String description;

    @NotNull(message = "Digite o valor do convite!")
    @PositiveOrZero(message = "O valor não pode ser negativo!")
    private Double value;


}
