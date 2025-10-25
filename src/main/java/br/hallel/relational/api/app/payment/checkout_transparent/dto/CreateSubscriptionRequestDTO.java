package br.hallel.relational.api.app.payment.checkout_transparent.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateSubscriptionRequestDTO(
        @NotBlank(message = "Plan ID cannot be blank")
        String planId, // O ID do Plano (Plan ID)

        @NotBlank(message = "Token cannot be blank")
        String token, // Token do cartão gerado pelo frontend

        @NotBlank(message = "Payer email cannot be blank")
        String payerEmail,

        @NotBlank(message = "Payer first name cannot be blank")
        String payerFirstName,

        @NotBlank(message = "Payer last name cannot be blank")
        String payerLastName,

        @NotBlank(message = "Payer identification number cannot be blank")
        String payerIdentificationNumber,

        String description // Motivo da assinatura
) {
}