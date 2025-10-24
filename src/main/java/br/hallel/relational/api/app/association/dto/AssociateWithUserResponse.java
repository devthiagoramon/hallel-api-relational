package br.hallel.relational.api.app.association.dto;

import br.hallel.relational.api.app.association.model.AssociatePaymentStatus;
import br.hallel.relational.api.app.user.model.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssociateWithUserResponse {
    private UUID associateId;
    private User user;
    private AssociatePaymentStatus status;
    private LocalDateTime associateSince;
    private LocalDateTime renewalDate;
}
