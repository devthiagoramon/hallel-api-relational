package br.hallel.relational.api.app.event.model;


import br.hallel.relational.api.app.ministry.model.Ministry;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "event_schedule_ministry")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventScheduleMinistry {

    @EmbeddedId
    private EventScheduleMinistryIds id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_schedule_id", insertable = false, updatable = false)
    @MapsId("eventScheduleId")
    private EventSchedule eventSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ministry_id", insertable = false, updatable = false)
    @MapsId("ministryId")
    private Ministry ministry;

    public EventScheduleMinistry(EventSchedule eventSchedule, Ministry ministry) {
        this.eventSchedule = eventSchedule;
        this.ministry = ministry;
    }
}
