package br.hallel.relational.api.app.payment.checkout_pro.dto;

import java.math.BigDecimal;

public record CreateChargeRequest(
        String email,
        String firstName,
        String lastName,
        String cpf,
        BigDecimal amount,
        String description
) {


}
