package br.hallel.relational.api.app.ministry.model;

import br.hallel.relational.api.app.event.model.EventScale;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "event_scale_repertory")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class EventScaleRepertory {

    @EmbeddedId
    private EventScaleRepertoryId id;

    public EventScaleRepertory(EventScaleRepertoryId id) {
        this.id = id;
    }
}
