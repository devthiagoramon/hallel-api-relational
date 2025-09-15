package br.hallel.relational.api.app.event.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Table(name = "event_transaction")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class EventTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "description", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(nullable = false)
    private Double value;

    @Column(name = "date_transaction")
    private Date dateTransaction;

    @Column(name = "is_editable")
    private Boolean isEditable;

    @Column(name = "receipt_payment_file_image")
    private String receiptPaymentFileImage;

    @Column(name = "mercadopago_payment_id")
    private Long mercadoPagoPaymentId;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

}
