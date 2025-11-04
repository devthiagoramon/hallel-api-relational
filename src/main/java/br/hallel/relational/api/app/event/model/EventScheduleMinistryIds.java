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
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventScheduleMinistryIds {

    @Column(name = "event_schedule_id")
    private UUID eventScheduleId;

    @Column(name = "ministry_id")
    private UUID ministryId;

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EventScheduleMinistryIds that = (EventScheduleMinistryIds) o;
        return Objects.equals(eventScheduleId, that.eventScheduleId) && Objects.equals(ministryId,
                that.ministryId);
    }

    @Override public int hashCode() {
        return Objects.hash(eventScheduleId, ministryId);
    }
}
