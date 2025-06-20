package br.hallel.relational.api.app.ministry.controller.coordinator;

import br.hallel.relational.api.app.ministry.dto.*;
import br.hallel.relational.api.app.ministry.service.RepertoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/coordinator/ministry/repertory")
@RequiredArgsConstructor
@Tag(name = "Coordinator Repertory Ministry", description = "Coordinator part for repertory ministry managment")
public class CoordinatorRepertoryController {

    @Autowired
    private RepertoryService service;

    @PostMapping("/create")
    public ResponseEntity<RepertoryResponse> createRepertory(@RequestBody RepertoryRequestDTO request) {
        return ResponseEntity.ok(this.service.createRepertory(request));
    }

    @GetMapping("/list-all")
    public ResponseEntity<List<RepertoryResponse>> listAllRepertories() {
        return ResponseEntity.ok(this.service.listAllRepertory());
    }

    @GetMapping("/list-all/ministry-id/{ministryId}")
    public ResponseEntity<List<RepertoryShortResponse>> listAllRepertoriesByMinistryId(
            @PathVariable("ministryId") UUID ministryId
    ) {
        return ResponseEntity.ok(this.service.listAllRepertoryByMinistryId(ministryId));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<RepertoryResponse> getRepertoryById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(this.service.getRepertoryById(id));
    }

    @GetMapping("/get-short-response/{id}")
    public ResponseEntity<RepertoryShortResponse> getRepertoryShortById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(this.service.getRepertoryShortById(id));
    }

    @GetMapping("/get/ministry/{id}")
    public ResponseEntity<List<RepertoryResponse>> listRepertoryByMinistryId(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(this.service.listRepertoryByMinistryId(id));
    }

    @PostMapping("/add-remove/music/{id}")
    public ResponseEntity<RepertoryResponse> addOrRemoveMusicToRepertory(@PathVariable("id") UUID idMinistry, @RequestBody MusicRepertoryAddRemoveDTO request) {
        return ResponseEntity.ok(this.service.addOrRemoveMusicsRepertory(idMinistry, request));
    }

    @PostMapping("/add-remove/dance/{id}")
    public ResponseEntity<RepertoryResponse> addOrRemoveDanceToRepertory(@PathVariable("id") UUID idMinistry, @RequestBody DanceRepertoryAddRemoveDTO request) {
        return ResponseEntity.ok(this.service.addOrRemoveDanceRepertory(idMinistry, request));
    }

    @GetMapping("/list/dances-musics/{id}")
    public ResponseEntity<RepertoryResponse> listRepertoryWithDancesAndMusic(@PathVariable("id") UUID idRepertory) {
        return ResponseEntity.ok(this.service.listRepertoryWithDancesAndMusic(idRepertory));
    }

    @GetMapping("/list/musics/{id}")
    public ResponseEntity<List<MusicResponse>> listMusicsByRepertoryId(@PathVariable("id") UUID idRepertory) {
        return ResponseEntity.ok(this.service.listMusicsByRepertoryId(idRepertory));
    }



    @GetMapping("/list/dances/{idRepertory}")
    public ResponseEntity<List<DanceResponse>> listDancesByRepertoryId(@PathVariable("idRepertory") UUID idRepertory) {
        return ResponseEntity.ok(this.service.listDancesByRepertoryId(idRepertory));
    }

}
