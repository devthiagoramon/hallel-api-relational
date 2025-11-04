package br.hallel.relational.api.app.payment.checkout_transparent.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PixPaymentData(
        Long paymentId,
        BigDecimal amount,
        String eventTitle,
        String pixCode,
        String qrCodeImageUrl,
        LocalDateTime expirationDateTime
) {
}
