package br.hallel.relational.api.app.event.controller.user_public;

import br.hallel.relational.api.app.event.dto.EventResponse;
import br.hallel.relational.api.app.event.dto.EventResponseWithMinistryAssociated;
import br.hallel.relational.api.app.event.dto.EventShortResponse;
import br.hallel.relational.api.app.event.dto.FilterEventDTO;
import br.hallel.relational.api.app.event.model.enum_type.EventStatus;
import br.hallel.relational.api.app.event.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/public/event")
@RequiredArgsConstructor
@Tag(name = "Events - Public", description = "Events part for public")
public class PublicEventController {

    @Autowired
    private EventService eventService;

    // ** CONSULTAS DE EVENTOS **
    @GetMapping("/list-all")
    @Operation(summary = "Listing all Events. Important and about to happen")
    public ResponseEntity<Page<EventResponse>> listAllEvents(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(this.eventService.listAllEventsHomePage(PageRequest.of(page, size)));
    }

    @GetMapping("/list-all/table-admin")
    @Operation(summary = "Listing all Events. Important and about to happen")
    public ResponseEntity<Page<EventResponse>> listAllEventsTableAdmin(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(this.eventService.listAllEvents(PageRequest.of(page, size)));
    }

    @GetMapping("/list-all/by-status")
    @Operation(summary = "Listing all Events. Important and about to happen")
    public ResponseEntity<Page<EventResponse>> listAllEventsByStatus(
            @RequestParam(name = "status") EventStatus status,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(this.eventService.listAllEventsByEventStatus(PageRequest.of(page, size), status));
    }

    @GetMapping("/list-all/happened")
    @Operation(summary = "List all events that have already happened")
    public ResponseEntity<Page<EventResponse>> getAllEventsAlredyHappened(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                this.eventService.listAllEventsByEventStatus(PageRequest.of(page, size), EventStatus.FINALIZADO));
    }

    @GetMapping("/retreat/list-all")
    public ResponseEntity<Page<EventResponse>> getAllRetreats(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(this.eventService.listAllRetreats(PageRequest.of(page, size)));
    }

    @GetMapping("/retreat/list-all/happened")
    @Operation(summary = "List all retreats that have already happened")
    public ResponseEntity<Page<EventResponse>> getAllRetreatsAlreadyHappened(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(this.eventService.listAllRetreatsAlreadyHappened(PageRequest.of(page, size)));
    }

    @GetMapping("/list-upcoming")
    public ResponseEntity<Page<EventResponse>> getAllEventsUpcoming(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(this.eventService.listAllUpcomingEvents(page, size));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<EventResponseWithMinistryAssociated> getEvent(@PathVariable(value = "id") UUID id) {
        EventResponseWithMinistryAssociated event = eventService.getEventById(id);
        if (event == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(event);
    }

    @GetMapping("/list-all/title/asc")
    public ResponseEntity<List<EventResponse>> getAllEventsByTitleAsc(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "filter", required = false, defaultValue = "undefined") String filter
    ) {
        EventStatus eventStatus = null;
        if (!filter.equalsIgnoreCase("undefined")) {
            FilterEventDTO filterEventDTO = FilterEventDTO.valueOf(filter);
            switch (filterEventDTO) {
                case FINALIZADOS -> eventStatus = EventStatus.FINALIZADO;
                case OCORRENDO -> eventStatus = EventStatus.OCORRENDO;
                case PROXIMOS -> eventStatus = EventStatus.AGENDADO;
            }
        }
        List<EventResponse> events = eventService.listEventsByTitleOrderByAsc(page, size, eventStatus);
        if (events.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(events);
    }

    @GetMapping("/list-all/by-title")
    public ResponseEntity<List<EventResponse>> getAllEventsByTitle(
            @RequestParam(name = "title") String title,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        return ResponseEntity.ok(this.eventService.listEventsByTitle(title, page, size));
    }

    @GetMapping("/list-all/date-exp")
    public ResponseEntity<Page<EventResponse>> getAllEventsByDateExp(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(this.eventService.listEventsByDateExp(PageRequest.of(page, size)));
    }

    @GetMapping("/list-all/importants")
    @Operation(summary = "List all important events that will happen", description = "Handles listing all important events that will happen in community")
    public ResponseEntity<List<EventShortResponse>> getAllEventsByImportants() {
        return ResponseEntity.ok(this.eventService.listEventsImportantThatWillHappen());
    }
}
