package br.hallel.relational.api.app.ministry.controller.coordinator;

import br.hallel.relational.api.app.ministry.dto.MusicAddEditDTO;
import br.hallel.relational.api.app.ministry.dto.MusicResponse;
import br.hallel.relational.api.app.ministry.service.MusicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/coordinator/ministry/music")
@RequiredArgsConstructor
@Tag(name = "Coordinator Music Ministry", description = "Coordinator part for music ministry managment")
public class CoordinatorMusicMinistryController {
    @Autowired
    private MusicService service;

    @PostMapping("/create")
    public ResponseEntity<MusicResponse> createMusic(@RequestBody MusicAddEditDTO music) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createMusic(music));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<MusicResponse> getMusicById(@PathVariable(name = "id") UUID id) {
        return ResponseEntity.ok(service.getMusicById(id));
    }

    @PutMapping("/{musicMinistryId}")
    @Operation(summary = "Edit music ministry", description = "Route to edit some music ministry with his id and infos to edit")
    public ResponseEntity<MusicResponse> updateMusic(@PathVariable(name = "musicMinistryId") UUID musicMinistryId,
                                                     @RequestBody MusicAddEditDTO music) {
        return ResponseEntity.ok(service.editMusic(musicMinistryId, music));
    }

    @GetMapping("/list-all")
    public ResponseEntity<List<MusicResponse>> listAllMusics() {
        return ResponseEntity.ok(service.listAllMusics());
    }
    @GetMapping("/list-all/ministry-id/{ministryId}")
    public ResponseEntity<List<MusicResponse>> listAllMusics(@PathVariable(name = "ministryId") UUID ministryId) {
        return ResponseEntity.ok(service.listAllMusicsByMinistryId(ministryId));
    }

    @DeleteMapping("/delete/{idMusic}")
    public ResponseEntity<?> deleteMusicById(@PathVariable(name = "idMusic") UUID id) {
        this.service.deleteMusicById(id);
        return ResponseEntity.noContent().build();
    }
}
