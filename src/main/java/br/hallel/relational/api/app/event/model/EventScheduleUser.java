package br.hallel.relational.api.app.event.model;

import br.hallel.relational.api.app.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "event_schedule_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventScheduleUser {

    @EmbeddedId
    private EventScheduleUserIds id;

    @JoinColumn(name = "event_schedule_id", insertable = false, updatable = false)
    @MapsId("eventScheduleId")
    @ManyToOne(fetch = FetchType.LAZY)
    private EventSchedule eventSchedule;

    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public EventScheduleUser(EventSchedule eventSchedule, User user) {
        this.eventSchedule = eventSchedule;
        this.user = user;
    }
}
