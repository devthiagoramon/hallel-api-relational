package br.hallel.relational.api.app.event.model;

import br.hallel.relational.api.app.ministry.model.AuditionMinistry;
import br.hallel.relational.api.app.ministry.model.Ministry;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Table(name = "event_scale")
@Entity
public class EventScale {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "event_scale_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "ministry_id")
    private Ministry ministry;

    @OneToOne(mappedBy = "eventScale", cascade = CascadeType.ALL)
    private AuditionMinistry auditionMinistryList;
}
