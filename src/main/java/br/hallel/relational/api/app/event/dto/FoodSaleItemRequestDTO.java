package br.hallel.relational.api.app.event.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record FoodSaleItemRequestDTO(
        UUID foodId,
        Integer quantity,
        BigDecimal price
) {
}
