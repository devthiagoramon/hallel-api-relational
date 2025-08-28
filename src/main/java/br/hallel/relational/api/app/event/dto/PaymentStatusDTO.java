package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.StatusPaymentEventParticipation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentStatusDTO {
    private String qrCodeBase64;
    private String qrCode;
    private StatusPaymentEventParticipation statusPaymentEventParticipation;
}
