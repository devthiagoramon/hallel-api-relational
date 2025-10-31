package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.enum_type.StatusPaymentEventParticipation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class UserPaymentDetailResponse {
    private UUID eventId;
    private UUID userId;
    private String eventName;
    private String userName;
    private double valuePaid;
    private double eventValue;
    private OffsetDateTime paymentDate;
    private StatusPaymentEventParticipation paymentStatus;
    private String receiptBase64;
    private String receiptPDFBase64;

}
