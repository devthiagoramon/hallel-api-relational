package br.hallel.relational.api.app.event.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventScheduleVisitorIds {

    @Column(name = "event_schedule_id")
    private UUID eventScheduleId;

    @Column(name = "email")
    private String email;

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EventScheduleVisitorIds that = (EventScheduleVisitorIds) o;
        return Objects.equals(eventScheduleId, that.eventScheduleId) && Objects.equals(email,
                that.email);
    }

    @Override public int hashCode() {
        return Objects.hash(eventScheduleId, email);
    }
}
