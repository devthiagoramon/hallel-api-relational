package br.hallel.relational.api.app.payment.checkout_transparent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreatePlanRequestDTO(
        @NotBlank String planName,
        @NotNull BigDecimal amount,
        @NotBlank String reason,
        @NotNull Integer frequency, // Ex: 1
        @NotBlank String frequencyType // Ex: "months" ou "days"
) {}