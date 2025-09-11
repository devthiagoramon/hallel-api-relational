package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.Foods;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FoodResponseDTO {
    private UUID id;
    private String name;
    private BigDecimal value;
    private Integer quantity;
    private LocalDateTime registeredDate;
    private UUID eventId;

    public FoodResponseDTO toResponse(Foods foods) {
        return new FoodResponseDTO(
                foods.getId(),
                foods.getName(),
                foods.getValue(),
                foods.getStockQuantity(),
                foods.getRegisteredDate(),
                foods.getEvent().getId()
        );
    }
}
