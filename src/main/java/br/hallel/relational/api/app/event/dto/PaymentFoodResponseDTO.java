package br.hallel.relational.api.app.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter@Setter
@AllArgsConstructor@NoArgsConstructor
public class PaymentFoodResponseDTO {
    private String pixCode;
    private String qrCodeBase64;
    private UUID paymentId;
}
