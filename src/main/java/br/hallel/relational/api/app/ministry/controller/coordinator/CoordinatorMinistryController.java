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
@RequestMapping("/coordinator/ministry")
@RequiredArgsConstructor
@Tag(name = "Coordinator Ministry", description = "Coordinator part for ministr managment")
public class CoordinatorMinistryController {

    @Autowired
    private MinistryService service;

    @GetMapping("/list-all/scales/{idMinistry}")
    public ResponseEntity<List<EventScaleResponse>> listAllScalesByMinistryId(@PathVariable(name = "idMinistry") UUID idMinistry) {
        return ResponseEntity.ok(this.service.listAllEventScalesByMinistryId(idMinistry));
    }




}
