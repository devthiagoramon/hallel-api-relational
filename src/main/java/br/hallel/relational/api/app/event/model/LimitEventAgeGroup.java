package br.hallel.relational.api.app.event.model;

import br.hallel.relational.api.app.event.model.enum_type.AgeGroup;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "limit_event_age_group")
@Data
public class LimitEventAgeGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "age_group")
    @Enumerated(EnumType.STRING)
    private AgeGroup ageGroup;
    @Column(name = "limit_quantity")
    private Integer limitQuantity;
    @Column(name = "current_quantity")
    private Integer currentQuantity;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
}
