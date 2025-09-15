package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.FoodSaleItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventFoodSoldResponseDTO {
    private UUID id;
    private String name;
    private UUID eventId;
    private UUID foodId;
    private Integer quantity;
    private Double price;
    private Double total;
    private LocalDateTime soldAt;


    public static EventFoodSoldResponseDTO toResponseDTO(FoodSaleItem e) {
        return new EventFoodSoldResponseDTO(
                e.getId(),
                e.getFood().getName(),
                e.getEvent().getId(),
                e.getFood().getId(),
                e.getQuantity(),
                e.getPrice(),
                e.getPrice() * e.getQuantity(),
                e.getSoldAt()
        );
    }
}
