package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class EventByEventScaleResponse {
    private UUID id;
    private String title;
    private String description;
    private Date date;
    private String local_event_name;

    public EventByEventScaleResponse toEventByEventScaleResponse(Event event) {
        return new EventByEventScaleResponse(event.getId(), event.getTitle(), event.getDescription(), event.getDate(), event.getLocal_event_name());
    }
}
