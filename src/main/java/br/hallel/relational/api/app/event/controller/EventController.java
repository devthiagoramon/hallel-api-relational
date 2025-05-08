package br.hallel.relational.api.app.event.controller;

import br.hallel.relational.api.app.event.dto.EventDTO;
import br.hallel.relational.api.app.event.dto.EventHomePageResponse;
import br.hallel.relational.api.app.event.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping("/create")
    public EventDTO createEvent(@RequestBody EventDTO eventDTO) {
        return eventService.create(eventDTO,null,null);
    }

    @GetMapping("")
    public List<EventHomePageResponse> getAllEvents() {
        return this.eventService.listEventsToHomePage();
    }
}
