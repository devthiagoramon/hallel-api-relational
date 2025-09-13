package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.EventFoodSales;
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
public class EventFoodSoldResponseDTO {
    private UUID id;
    private UUID eventId;
    private UUID foodId;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal total;
    private LocalDateTime soldAt;


    public EventFoodSoldResponseDTO toResponseDTO(EventFoodSales e) {
        return new EventFoodSoldResponseDTO(
                e.getId(),
                e.getEvent().getId(),
                e.getFood().getId(),
                e.getQuantity(),
                e.getPrice(),
                e.getPrice().multiply(BigDecimal.valueOf(e.getQuantity())),
                e.getSoldAt()
        );
    }
}
