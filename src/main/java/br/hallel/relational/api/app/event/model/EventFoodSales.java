package br.hallel.relational.api.app.event.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "event_foods_sale")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventFoodSales {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "food_id")
    private Foods food;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "sold_at")
    private LocalDateTime soldAt;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "event_transaction_id")
    private EventTransaction eventTransaction;

}
