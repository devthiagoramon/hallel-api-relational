package br.hallel.relational.api.app.event.model;

import br.hallel.relational.api.app.ministry.model.AuditionMinistry;
import br.hallel.relational.api.app.ministry.model.Ministry;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Table(name = "event_scale")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventScale {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "ministry_id")
    private Ministry ministry;

    public EventScale(Event event, Ministry ministry) {
        this.event = event;
        this.ministry = ministry;
    }
}
