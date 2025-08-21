package br.hallel.relational.api.app.payment.checkout_transparent.dto;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreatePixPaymentRequestDTO(
        @NotNull(message = "Amount cannot be null")
        @DecimalMin(value = "0.01", message = "Amount must be positive")
        BigDecimal amount,

        @NotBlank(message = "Description cannot be blank")
        String description,

        @NotBlank(message = "Payer email cannot be blank")
        String payerEmail,

        @NotBlank(message = "Payer first name cannot be blank")
        String payerFirstName,

        @NotBlank(message = "Payer last name cannot be blank")
        String payerLastName,

        @NotBlank(message = "Payer CPF/CNPJ cannot be blank")
        String payerIdentificationNumber
) {}