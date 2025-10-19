package br.hallel.relational.api.app.association.dto;

import br.hallel.relational.api.app.association.model.Associate;
import br.hallel.relational.api.app.association.model.AssociatePaymentStatus;
import br.hallel.relational.api.app.association.model.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssociateResponse {
    private UUID associateId;
    private UUID userId;
    private AssociatePaymentStatus status;
    private LocalDateTime associateSince;
    private LocalDateTime renewalDate;

    public static AssociateResponse toResponse(Associate associate) {
        return new AssociateResponse(
                associate.getId(),
                associate.getUser().getId(),
                associate.getStatus(),
                associate.getAssociateSince(),
                associate.getRenewalDate()
        );
    }

}
