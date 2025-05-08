package br.hallel.relational.api.app.event.interfaces;

import br.hallel.relational.api.app.event.dto.EventDTO;
import br.hallel.relational.api.app.event.dto.EventHomePageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface EventInterface {
    EventDTO create(EventDTO eventDTO, MultipartFile fileImage, MultipartFile fileBanner
    );

    List<EventHomePageResponse> listEventsToHomePage();

    EventHomePageResponse getEventById(UUID id);

    EventDTO updateById(UUID id, EventDTO eventDTO);

    EventDTO deleteById(UUID id);

    List<EventHomePageResponse> listEventsByTitleAsc();
    List<EventHomePageResponse> listEventsByDateExp();
}
