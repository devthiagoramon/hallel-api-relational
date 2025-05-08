package br.hallel.relational.api.app.event.model;

import jakarta.persistence.*;

import java.util.UUID;

@Table(name = "event_scale")
@Entity
public class EventScale {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
}
