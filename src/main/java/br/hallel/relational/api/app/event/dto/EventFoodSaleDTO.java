package br.hallel.relational.api.app.event.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record EventFoodSaleDTO(
         Integer quantity,
         BigDecimal price
) {
}
