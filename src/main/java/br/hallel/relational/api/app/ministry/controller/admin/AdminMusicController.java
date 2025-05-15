package br.hallel.relational.api.app.ministry.controller.admin;

import br.hallel.relational.api.app.ministry.dto.MusicAddEditDTO;
import br.hallel.relational.api.app.ministry.dto.MusicResponse;
import br.hallel.relational.api.app.ministry.service.MuiscService;
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
@RequestMapping("/admin/ministry/music")
@RequiredArgsConstructor
@Tag(name = "Admin Music Ministry", description = "Admin part for Music ministry managment")
public class AdminMusicController {
    @Autowired
    private MuiscService service;

    @PostMapping("/create")
    public ResponseEntity<MusicResponse> createMusic(@RequestBody MusicAddEditDTO music) {
        return ResponseEntity.ok(service.createMuisc(music));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<MusicResponse> getMusicById(@PathVariable(name = "id") UUID id) {
        return ResponseEntity.ok(service.getMuiscById(id));
    }

    @GetMapping("/list-all")
    public ResponseEntity<List<MusicResponse>> listAllMuiscs() {
        return ResponseEntity.ok(service.listAllMusics());
    }

    @DeleteMapping("/delete/{idMusic}")
    public ResponseEntity<?> deleteMusicById(@PathVariable(name = "idMusic") UUID id) {
        this.service.deleteMuiscById(id);
        return ResponseEntity.ok().build();
    }
}
