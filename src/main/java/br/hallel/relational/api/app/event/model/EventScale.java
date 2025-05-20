package br.hallel.relational.api.app.event.model;

import br.hallel.relational.api.app.ministry.model.Ministry;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
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

    @Column(name = "date")
    private Date date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id")
    @ToString.Exclude
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ministry_id")
    @ToString.Exclude
    private Ministry ministry;

    public EventScale(Event event, Ministry ministry) {
        this.event = event;
        this.ministry = ministry;
    }
}
