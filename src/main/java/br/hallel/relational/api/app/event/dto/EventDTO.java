package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.Event;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.UUID;

public record EventDTO(
        String title,
        String description,
        Date date,
        String local_event_name,
        double local_event_longitude,
        double local_event_latitude
) {
    public Event toEvent(EventDTO eventDTO) {
        Event event = new Event();
        event.setTitle(eventDTO.title());
        event.setDescription(eventDTO.description());
        event.setDate(eventDTO.date());
        event.setLocal_event_name(eventDTO.local_event_name());
        event.setLocal_event_longitude(eventDTO.local_event_longitude());
        event.setLocal_event_latitude(eventDTO.local_event_latitude());
        return event;
    }
}
