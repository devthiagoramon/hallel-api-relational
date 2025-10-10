package br.hallel.relational.api.app.association.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssociationPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "associate_id")
    private Associate associate;

    // O pagamento foi para qual mês/ano? (exemplo: 2025-10)
    private YearMonth referenceMonth;

    // O pagamento cobre quantos meses? (Pode vir do DTO de criação, ex: 1 ou 12)
    private int monthsCovered = 1;

    // O valor exato pago
    private BigDecimal valuePaid;

    private LocalDateTime paidDate;

    private Long mercadoPagoPaymentId;

    private String pixTxid;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private AssociatePaymentStatus status;
}