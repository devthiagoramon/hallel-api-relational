package br.hallel.relational.api.app.ministry.controller.coordinator;

import br.hallel.relational.api.app.event.dto.EventScaleResponse;
import br.hallel.relational.api.app.ministry.dto.MinistryRequestDTO;
import br.hallel.relational.api.app.ministry.dto.MinistryResponse;
import br.hallel.relational.api.app.ministry.model.Ministry;
import br.hallel.relational.api.app.ministry.service.MinistryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/public/ministry")
@RequiredArgsConstructor
@Tag(name = "Coordinator Ministry", description = "Coordinator part for ministr managment")
public class CoordinatorMinistryController {

    @Autowired
    private MinistryService service;

    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
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

    @GetMapping("/list-all/scales/{idMinistry}")
    public ResponseEntity<List<EventScaleResponse>> listAllScalesByMinistryId(@PathVariable(name = "idMinistry") UUID idMinistry) {
        return ResponseEntity.ok(this.service.listAllEventScalesByMinistryId(idMinistry));
    }




}
