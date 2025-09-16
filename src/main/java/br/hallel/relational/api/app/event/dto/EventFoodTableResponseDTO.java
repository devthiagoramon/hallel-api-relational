package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.Foods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor @NoArgsConstructor
public class EventFoodTableResponseDTO {
    private UUID id;
    private String name;
    private Double value;
    private Integer stockQuantity;
    private Long quantitySale;
    private LocalDateTime registeredDate;
    private UUID eventId;

    public static EventFoodTableResponseDTO toResponse(Foods foods, Long quantitySale) {
        return new EventFoodTableResponseDTO(
                foods.getId(),
                foods.getName(),
                foods.getValue(),
                foods.getStockQuantity(),
                quantitySale,
                foods.getRegisteredDate(),
                foods.getEvent().getId()
        );
    }
}
