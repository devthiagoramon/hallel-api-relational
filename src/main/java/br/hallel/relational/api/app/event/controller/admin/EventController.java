package br.hallel.relational.api.app.event.controller.admin;

import br.hallel.relational.api.app.event.dto.EventDTO;
import br.hallel.relational.api.app.event.dto.EventResponse;
import br.hallel.relational.api.app.event.service.EventService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/admin/event")
@RequiredArgsConstructor
@Tag(name = "Admin Event", description = "Admin part for event managment")
public class EventController {

    @Autowired
    private EventService eventService;

    //** CRIANDO EVENTO **
    @PostMapping("/create")
    public ResponseEntity<EventResponse> createEvent(@RequestPart(name = "request") EventDTO eventDTO, @RequestPart(name = "image_url") MultipartFile img_url, @RequestPart(name = "banner_url") MultipartFile banner_url) {
        return ResponseEntity.ok(eventService.create(eventDTO, img_url, banner_url));
    }

    @PatchMapping("/edit/{id}")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable(name = "id") UUID id,
                                                     @RequestPart(name = "request") EventDTO eventDTO,
                                                     @RequestPart(name = "image_url") MultipartFile img_url,
                                                     @RequestPart(name = "banner_url") MultipartFile banner_url) {

        return ResponseEntity.ok(this.eventService.updateById(id, eventDTO, img_url, banner_url));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteEvent(@PathVariable UUID id) {
        boolean deleted = this.eventService.deleteById(id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", deleted);
        response.put("message", deleted
                ? "Evento deletado com sucesso."
                : "Evento não encontrado ou não pôde ser deletado.");

        return ResponseEntity.ok(response);
    }

    // ** CONSULTAS DE EVENTOS **
    @GetMapping("/list-all")
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        return ResponseEntity.ok(this.eventService.listEventsToHomePage());
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
    public ResponseEntity<List<EventResponse>> getAllEventsByTitle() {
        List<EventResponse> events = eventService.listEventsByTitleAsc();
        if (events.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(events);
    }

    @GetMapping("/list-all/date-exp")
    public ResponseEntity<List<EventResponse>> getAllEventsByDateExp() {
        List<EventResponse> events = eventService.listEventsByDateExp();
        if (events.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(events);
    }
}
