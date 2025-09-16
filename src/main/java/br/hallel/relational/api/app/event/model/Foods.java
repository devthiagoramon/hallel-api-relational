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
@Table(name = "foods")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Foods {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "name")
    private String name;

    @Column(name = "value")
    private Double value;

    @Column(name = "stockQuantity")
    private Integer stockQuantity;

    @Column(name = "registered_date")
    private LocalDateTime registeredDate;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FoodSaleItem> saleItems = new ArrayList<>();
}
