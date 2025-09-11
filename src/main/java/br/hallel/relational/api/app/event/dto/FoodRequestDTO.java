package br.hallel.relational.api.app.event.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record FoodRequestDTO(
        @NotBlank(message = "Name cannot Blank or Null")
        @Valid
        String name,

        @NotNull @DecimalMin(value = "0.01", message = "Total amount must be positive")
        BigDecimal value,

        @NotNull @DecimalMin(value = "1", message = "Total amount must be positive")
        Integer stockQuantity,

        @NotNull(message = "Date cannot Blank")
        @Valid
        LocalDateTime registeredDate,

        @NotNull(message = "Event Id cannot Null")
        @Valid
        UUID eventId
) {
}
