package br.hallel.relational.api.app.ministry.controller.coordinator;

import br.hallel.relational.api.app.ministry.dto.DanceAddEditDTO;
import br.hallel.relational.api.app.ministry.dto.DanceResponse;
import br.hallel.relational.api.app.ministry.dto.DanceResponseShort;
import br.hallel.relational.api.app.ministry.service.DanceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/coordinator/ministry/dance")
@RequiredArgsConstructor
@Tag(name = "Coordinator Dance Ministry", description = "Coordinator part for Dance ministry managment")
public class CoordinatorDanceController {
    @Autowired
    private DanceService service;
    @Autowired
    private DanceService danceService;

    @PostMapping("/create")
    public ResponseEntity<DanceResponse> createMusic(@RequestBody DanceAddEditDTO music) {
        return ResponseEntity.ok(service.createDance( music));
    }
    @GetMapping("/get/{id}")
    public ResponseEntity<DanceResponse> getMusicById(@PathVariable(name = "id") UUID id) {
        return ResponseEntity.ok(service.getDanceById(id));
    }
    @GetMapping("/list-all")
    public ResponseEntity<List<DanceResponse>> listAllDances() {
        return ResponseEntity.ok(service.listAllDances());
    }
    @DeleteMapping("/delete/{idDance}")
    public ResponseEntity<?> removeDanceById(@PathVariable(name = "idDance") UUID id) {
        service.deleteDanceById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{idDance}")
    public ResponseEntity<DanceResponseShort> updateDance(@PathVariable("idDance") UUID danceId, @RequestBody DanceAddEditDTO dance) {
        return ResponseEntity.ok(this.danceService.editDance(danceId, dance));
    }

}
