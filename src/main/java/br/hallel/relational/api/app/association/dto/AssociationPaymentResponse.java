package br.hallel.relational.api.app.association.dto;

import br.hallel.relational.api.app.association.model.AssociatePaymentStatus;
import br.hallel.relational.api.app.association.model.AssociationPayment;
import br.hallel.relational.api.app.association.model.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssociationPaymentResponse {
    private UUID id;
    private String referenceMonth;
    private int monthsCovered;
    private String valuePaid;
    private LocalDateTime paidDate;
    private PaymentMethod paymentMethod;
    private AssociatePaymentStatus status;

    public static AssociationPaymentResponse toResponse(AssociationPayment obj) {
        return new AssociationPaymentResponse(
                obj.getId(),
                obj.getReferenceMonth() == null ? null : obj.getReferenceMonth().toString(),
                obj.getMonthsCovered(),
                obj.getValuePaid().toString(),
                obj.getPaidDate() ,
                obj.getPaymentMethod(),
                obj.getStatus()
        );
    }
}
