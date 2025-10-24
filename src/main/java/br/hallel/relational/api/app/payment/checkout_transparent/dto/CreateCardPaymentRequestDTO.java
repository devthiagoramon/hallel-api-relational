package br.hallel.relational.api.app.payment.checkout_transparent.dto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateCardPaymentRequestDTO(
        @NotNull(message = "Amount cannot be null")
        @DecimalMin(value = "0.01", message = "Amount must be positive")
        BigDecimal amount,

        String description,

        @NotBlank(message = "Token cannot be blank")
        String token, // Token gerado pelo frontend (Mercado Pago SDKs)

        @NotNull(message = "Installments cannot be null")
        Integer installments, // Número de parcelas

        // Dados do pagador
        @NotBlank(message = "Payer email cannot be blank")
        String payerEmail,

        @NotBlank(message = "Payer first name cannot be blank")
        String payerFirstName,

        @NotBlank(message = "Payer last name cannot be blank")
        String payerLastName,

        @NotBlank(message = "Payer identification number cannot be blank")
        String payerIdentificationNumber,

        @NotBlank(message = "Payment method ID cannot be blank")
        String paymentMethodId // O ID do método de pagamento (ex: visa, mastercard)
) {
}
