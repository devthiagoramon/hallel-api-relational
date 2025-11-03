// Crie este arquivo em: br/hallel/relational/api/app/event/dto/EventBatchStatusDTO.java
package br.hallel.relational.api.app.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventBatchStatusDTO {

    private UUID batchId;

    // Para o usuário ver "Lote 1", "Lote 2", etc.
    private int batchOrder;

    private int maxNumber;

    private Double valueIncrease;

    // O campo principal da sua solicitação
    private boolean isCurrent;
}