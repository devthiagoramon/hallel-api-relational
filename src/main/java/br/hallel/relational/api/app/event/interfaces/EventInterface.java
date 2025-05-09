package br.hallel.relational.api.app.event.interfaces;

import br.hallel.relational.api.app.event.dto.EventDTO;
import br.hallel.relational.api.app.event.dto.EventResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface EventInterface {
    EventResponse create(EventDTO eventDTO, MultipartFile fileImage, MultipartFile fileBanner
    );

    List<EventResponse> listEventsToHomePage();

    EventResponse getEventById(UUID id);

    EventResponse updateById(UUID id, EventDTO eventDTO, MultipartFile img_url,  MultipartFile banner_url);

    Boolean deleteById(UUID id);

    List<EventResponse> listEventsByTitleAsc();
    List<EventResponse> listEventsByDateExp();
}
