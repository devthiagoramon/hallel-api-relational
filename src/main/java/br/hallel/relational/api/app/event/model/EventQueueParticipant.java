package br.hallel.relational.api.app.event.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;


@Entity
@Data
@NoArgsConstructor
public class EventQueueParticipant {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_participation_id")
    private EventParticipation eventParticipation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Event event;

    private OffsetDateTime queuedAt;

    private Boolean notified;

    public EventQueueParticipant(EventParticipation eventParticipation, Event event) {
        this.eventParticipation = eventParticipation;
        this.event = event;
        this.queuedAt = OffsetDateTime.now();
        this.notified = false;
    }
}

