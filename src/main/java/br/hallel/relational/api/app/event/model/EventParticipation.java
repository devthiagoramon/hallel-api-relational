package br.hallel.relational.api.app.event.model;

import br.hallel.relational.api.app.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Table(name = "event_participation")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventParticipation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPaymentEventParticipation statusPaymentEventParticipation;

    @Column(nullable = false)
    private String community;

    @Column(nullable = false)
    private Boolean hasParticipated;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "user_function_in_event")
    private UserFunctionInEvent userFunctionInEvent;

    @Column(name = "amount_paid")
    private Double amountPaid;

    @Column(name = "pix_txid")
    private String pixTxid;

    @Column(name = "mercadopago_payment_id")
    private Long mercadoPagoPaymentId;

    @Column(name = "paid_date")
    private OffsetDateTime paidDate;
}