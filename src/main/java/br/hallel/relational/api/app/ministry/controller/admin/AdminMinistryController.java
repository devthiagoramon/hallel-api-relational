package br.hallel.relational.api.app.ministry.controller.admin;

import br.hallel.relational.api.app.event.service.EventScaleService;
import br.hallel.relational.api.app.ministry.dto.MinistryRequestDTO;
import br.hallel.relational.api.app.ministry.dto.MinistryResponse;
import br.hallel.relational.api.app.ministry.dto.MinistrySimpleResponse;
import br.hallel.relational.api.app.ministry.service.MinistryService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/admin/ministry")
@RequiredArgsConstructor
@Tag(name = "Admin Ministry", description = "Admin part for ministry managment")
public class AdminMinistryController {

    @Autowired
    private MinistryService service;
    @Autowired
    private EventScaleService eventScaleService;

    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    @Operation(summary = "Create a new ministry into system")
    public ResponseEntity<MinistryResponse> createMinistry(
            @RequestPart(name = "request") MinistryRequestDTO ministry,
            @RequestPart(name = "image") MultipartFile image) {
        return ResponseEntity.ok(this.service.createMinistry(ministry, image));
    }

    @PutMapping(value = "/edit/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<MinistryResponse> editMinistry(
            @PathVariable(name = "id") UUID id,
            @RequestPart(name = "request") MinistryRequestDTO ministry,
            @RequestPart(name = "image", required = false) MultipartFile image) {
        return ResponseEntity.ok(this.service.editMinistry(id, ministry, image));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteEvent(@PathVariable(name = "id") UUID id) {

        Map<String, String> response = new HashMap<>();
        response.put("message", "Evento deletado com sucesso!");
        response.put("Ministry: ", this.service.deleteMinistryById(id).toString());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/list-all/by-event/{eventId}")
    public ResponseEntity<List<MinistrySimpleResponse>>
    listAllMinistriesByEventId(@PathVariable(name = "eventId") UUID eventId) {
        return ResponseEntity.ok(this.eventScaleService.listMinistriesByEventId(eventId));
    }

}
