package br.hallel.relational.api.app.event.dto;

import jakarta.validation.constraints.DecimalMin;

import java.time.LocalDateTime;
import java.util.UUID;

public record FoodEditDTO(
        String name,
        @DecimalMin(value = "0.01") Double value,
        Integer stockQuantity,
        LocalDateTime registeredDate,
        UUID eventId

) {
}
