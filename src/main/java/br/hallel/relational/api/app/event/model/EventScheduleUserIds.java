package br.hallel.relational.api.app.event.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Embeddable
public class EventScheduleUserIds {


    @Column(name = "event_schedule_id")
    private UUID eventScheduleId;

    @Column(name = "event_schedule_id")
    private UUID userId;

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EventScheduleUserIds that = (EventScheduleUserIds) o;
        return Objects.equals(eventScheduleId, that.eventScheduleId) && Objects.equals(userId,
                that.userId);
    }

    @Override public int hashCode() {
        return Objects.hash(eventScheduleId, userId);
    }
}
