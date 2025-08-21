package br.hallel.relational.api.app.event.controller.admin;

import br.hallel.relational.api.app.event.dto.*;
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
public class AdminEventController {

    @Autowired
    private EventService eventService;

    //** CRIANDO EVENTO **
    @PostMapping(value = "/create", consumes = "multipart/form-data")
    public ResponseEntity<EventResponse> createEvent(@RequestPart(name = "request") EventDTO eventDTO,
                                                     @RequestPart(name = "image_url") MultipartFile img_url,
                                                     @RequestPart(name = "banner_url") MultipartFile banner_url) {
        return ResponseEntity.ok(eventService.create(eventDTO, img_url, banner_url));
    }

    @PatchMapping(value = "/edit/{id}", consumes = "multipart/form-data")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable(name = "id") UUID id,
                                                     @RequestPart(name = "request") EventDTO eventDTO,
                                                     @RequestPart(name = "image_url", required = false) MultipartFile img_url,
                                                     @RequestPart(name = "banner_url", required = false) MultipartFile banner_url) {
        return ResponseEntity.ok(this.eventService.updateById(id, eventDTO, img_url, banner_url));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteEvent(@PathVariable(name = "id") UUID id) {
        this.eventService.getEventById(id);
        boolean deleted = this.eventService.deleteById(id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", deleted);
        response.put("message", deleted
                ? "Evento deletado com sucesso."
                : "Evento não encontrado ou não pôde ser deletado.");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/create/transaction")
    public ResponseEntity<EventTransactionResponse> create(@RequestBody EventTransactionDTO dto) {
        return ResponseEntity.ok(eventService.addTransaction(dto));
    }

    @GetMapping("/transaction/list-all/by-event/{eventId}")
    public ResponseEntity<List<EventTransactionResponse>> listAllTransactionsByEvent(@PathVariable UUID eventId) {
        return ResponseEntity.ok(eventService.listAllTransactionsByEvent(eventId));
    }
    @GetMapping("/transaction/by-id/{eventId}")
    public ResponseEntity<EventTransactionResponse> findTransactionById(@PathVariable UUID eventId) {
        return ResponseEntity.ok(eventService.findTransactionById(eventId));
    }

    @PutMapping("/transaction/edit/{id}")
    public ResponseEntity<EventTransactionResponse> updateTransaction(@PathVariable UUID id, @RequestBody EventTransactionDTO dto) {
        return ResponseEntity.ok(eventService.updateTransaction(id, dto));
    }

    @DeleteMapping("/transaction/delete/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable UUID id) {
        eventService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
