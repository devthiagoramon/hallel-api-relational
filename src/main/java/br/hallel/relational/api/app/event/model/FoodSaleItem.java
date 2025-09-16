package br.hallel.relational.api.app.event.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "food_sale_items")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FoodSaleItem {

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
    private Double price;

    @Column(name = "sold_at")
    private LocalDateTime soldAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_transaction_id", nullable = false)
    private FoodTransaction transaction;

}
