package br.hallel.relational.api.app.event.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "food_transactions")
public class FoodTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Double value;

    private String description;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    private Long mercadoPagoPaymentId;

    @Enumerated(EnumType.STRING)
    private StatusPaymentFood status;

    @Column(nullable = false)
    private OffsetDateTime dateTransaction;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FoodSaleItem> saleItems;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "event_transaction_id", referencedColumnName = "id")
    private EventTransaction eventTransaction;
}