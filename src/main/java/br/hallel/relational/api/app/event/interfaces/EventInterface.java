package br.hallel.relational.api.app.event.interfaces;

import br.hallel.relational.api.app.event.dto.EventDTO;
import br.hallel.relational.api.app.event.dto.EventResponse;
import br.hallel.relational.api.app.event.dto.EventResponseWithMinistryAssociated;
import br.hallel.relational.api.app.event.dto.EventShortResponse;
import br.hallel.relational.api.app.event.model.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface EventInterface {
    EventResponse create(EventDTO eventDTO, MultipartFile fileImage, MultipartFile fileBanner
    );

    Page<EventResponse> listAllEvents(Pageable pageable);

    EventResponseWithMinistryAssociated getEventById(UUID id);

    EventResponse updateById(UUID id, EventDTO eventDTO, MultipartFile img_url, MultipartFile banner_url);

    Boolean deleteById(UUID id);

    EventShortResponse listEventInScaleInfo(UUID id);

    List<EventResponse> listEventsByTitleOrderByAsc(int page, int size, EventStatus eventStatus);

    List<EventResponse> listEventsByTitle(String title, int page, int size);

    Page<EventResponse> listEventsByDateExp(Pageable pageable);

    List<EventShortResponse> listEventsImportantThatWillHappen();
}
