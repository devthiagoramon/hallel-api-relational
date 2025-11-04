package br.hallel.relational.api.app.event.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "event_schedule_visitor")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventScheduleVisitor {

    @EmbeddedId
    private EventScheduleVisitorIds id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_schedule_id", insertable = false, updatable = false)
    @MapsId("eventScheduleId")
    private EventSchedule eventSchedule;

    @Column(nullable = false)
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "date_birth")
    private OffsetDateTime dateBirth;
}
