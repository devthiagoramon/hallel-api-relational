package br.hallel.relational.api.app.ministry.controller.coordinator;

import br.hallel.relational.api.app.ministry.dto.AuditionDTO;
import br.hallel.relational.api.app.ministry.dto.AuditionResponse;
import br.hallel.relational.api.app.ministry.dto.EventScaleSimpleResponse;
import br.hallel.relational.api.app.ministry.service.AuditionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/user/audition")
@RequiredArgsConstructor
@Tag(name = "Coordinator - Audition", description = "Coordinator part for auditions managment")
public class CoordinatorAuditionMinistryController {
    @Autowired
    private AuditionService service;

    @PostMapping("/create")
    public ResponseEntity<AuditionResponse> createAudition(@RequestBody AuditionDTO request) {
        return ResponseEntity.ok(this.service.createAudition(request));
    }

    @PutMapping("/edit/{auditionId}")
    public ResponseEntity<AuditionResponse> editAuditionById(
            @PathVariable(name = "auditionId") UUID id,
            @RequestBody AuditionDTO request) {
        return ResponseEntity.ok(this.service.updateAuditionById(id, request));
    }

    @GetMapping("/list-all")
    public ResponseEntity<List<AuditionResponse>> listAllAuditions() {
        return ResponseEntity.ok(this.service.listAllAuditions());
    }

    @GetMapping("/list-all/{ministryId}")
    public ResponseEntity<List<AuditionResponse>> listAllAuditionsByMinistryId(
            @PathVariable(name = "ministryId") UUID id
    ) {
        return ResponseEntity.ok(this.service.listAllAuditionsByMinistryId(id));
    }


    @GetMapping("/can-associate-scale")
    @Operation(
            summary = "Listar as escalas que podem ser associadas ao ensaios de um ministério a partir de uma data")
    public ResponseEntity<List<EventScaleSimpleResponse>> listEscalasAddableIntoEnsaioFromDate(
            @RequestParam(name = "ministryId") UUID ministerioId,
            @RequestParam(name = "from") LocalDateTime from) {
        return ResponseEntity.ok()
                .body(this.service.listScalesThatCanAssociateIntoEventScale(ministerioId, from));
    }


    @GetMapping("/get/{id}")
    public ResponseEntity<AuditionResponse> getAuditionById(
            @org.springframework.web.bind.annotation.PathVariable(name = "id") UUID id) {
        return ResponseEntity.ok(this.service.getAuditionById(id));
    }

    @DeleteMapping("/delete/{auditionId}")
    public ResponseEntity<?> deleteAuditionById(
            @org.springframework.web.bind.annotation.PathVariable(name = "id") UUID id) {
        this.service.deleteAuditionById(id);
        return ResponseEntity.ok().build();
    }

}
