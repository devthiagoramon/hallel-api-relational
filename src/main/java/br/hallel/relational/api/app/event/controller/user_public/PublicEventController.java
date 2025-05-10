package br.hallel.relational.api.app.event.controller.user_public;

import br.hallel.relational.api.app.event.dto.EventResponse;
import br.hallel.relational.api.app.event.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/public/event")
@RequiredArgsConstructor
public class PublicEventController {

    @Autowired
    private EventService eventService;

    // ** CONSULTAS DE EVENTOS **
    @GetMapping("/list-all")
    public ResponseEntity<List<EventResponse>> getAllEvents(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(this.eventService.listEventsToHomePage(page, size));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable(value = "id") UUID id) {
        EventResponse event = eventService.getEventById(id);
        if (event == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(event);
    }

    @GetMapping("/list-all/title")
    public ResponseEntity<List<EventResponse>> getAllEventsByTitle(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        List<EventResponse> events = eventService.listEventsByTitleAsc(page, size);
        if (events.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(events);
    }

    @GetMapping("/list-all/date-exp")
    public ResponseEntity<List<EventResponse>> getAllEventsByDateExp(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        List<EventResponse> events = eventService.listEventsByDateExp(page, size);
        if (events.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(events);
    }
}
