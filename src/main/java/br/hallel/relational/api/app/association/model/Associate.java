package br.hallel.relational.api.app.association.model;

import br.hallel.relational.api.app.user.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class Associate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Double valueAssociation;

    @Enumerated(EnumType.STRING)
    private AssociatePaymentStatus status;

    // data de associação
    private LocalDateTime associateSince;

    // data de expiração da associação
    private LocalDateTime renewalDate;

}
