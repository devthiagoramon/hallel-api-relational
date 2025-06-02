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
@RequestMapping("/coordinator/repertory")
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
    public ResponseEntity<List<RepertoryResponse>> listAllRepertoriesByMinistryId(
            @PathVariable("ministryId") UUID ministryId
    ) {
        return ResponseEntity.ok(this.service.listAllRepertoryByMinistryId(ministryId));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<RepertoryResponse> getRepertoryById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(this.service.getRepertoryById(id));
    }

    @GetMapping("/get/ministry/{id}")
    public ResponseEntity<List<RepertoryResponse>> listRepertoryByMinistryId(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(this.service.listRepertoryByMinistryId(id));
    }

    @PostMapping("/add-remove/music/{id}")
    public ResponseEntity<RepertoryResponse> addOrRemoveMusicToRepertory(@PathVariable("id") UUID idMinistry, @RequestBody MusicRepertoryAddRemoveDTO request) {
        return ResponseEntity.ok(this.service.addOrRemoveMusicsRepertory(idMinistry, request));
    }

    @PostMapping("/add-remove/playlist/{id}")
    public ResponseEntity<RepertoryResponse> addOrRemovePlaylistToRepertory(@PathVariable("id") UUID idMinistry, @RequestBody PlaylistAddRemoveDTO request) {
        return ResponseEntity.ok(this.service.addOrRemovePlaylistRepertory(idMinistry, request));
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

    @GetMapping("/list/playlists/{id}")
    public ResponseEntity<List<PlaylistResponse>> listPlaylistByRepertoryId(@PathVariable("id") UUID idRepertory) {
        return ResponseEntity.ok(this.service.listPlaylistsByRepertoryId(idRepertory));
    }

    @GetMapping(value = "/list/musics-dances/{idRepertory}")
    public ResponseEntity<RepertoryMusicAndDanceResponse> listMusicAndDancesByRepertoryId(@PathVariable("idRepertory") UUID idRepertory) {
        return ResponseEntity.ok(this.service.listMusicAndDanceByRepertoryId(idRepertory));
    }

    @GetMapping("/list/dances/{idRepertory}")
    public ResponseEntity<List<DanceResponse>> listDancesByRepertoryId(@PathVariable("idRepertory") UUID idRepertory) {
        return ResponseEntity.ok(this.service.listDancesByRepertoryId(idRepertory));
    }

    @PutMapping("/edit/dance/{idRepertory}/{idDance}")
    public ResponseEntity<DanceResponse> editDanceInRepertoryByIdRepertory(
            @PathVariable("idRepertory") UUID idRepertory,
            @PathVariable("iDance") UUID idDance, @RequestBody DanceAddEditDTO requestDTO) {
        return ResponseEntity.ok(this.service.editDanceRepertory(idRepertory, idDance, requestDTO));
    }

    @PutMapping("/edit/music/{idRepertory}/{idMusic}")
    public ResponseEntity<MusicResponse> editMusicInRepertoryByIdRepertory(
            @PathVariable("idRepertory") UUID idRepertory,
            @PathVariable("idMusic") UUID idMusic, @RequestBody MusicAddEditDTO requestDTO) {
        return ResponseEntity.ok(this.service.editMusicRepertory(idRepertory, idMusic, requestDTO));
    }
}
