package br.hallel.relational.api.app.ministry.controller.user_public;

import br.hallel.relational.api.app.ministry.dto.MusicWithoutMinistryResponse;
import br.hallel.relational.api.app.ministry.service.MusicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user/ministry/music")
@RequiredArgsConstructor
@Tag(name = "Member Music Ministry", description = "Member part for music ministry infos")
public class UserMusicMinistryController {

    private final MusicService musicService;


    @GetMapping("/{ministry-id}")
    @Operation(summary = "List all music of ministry", description = "Route for listing the music of some ministry by passing id of ministry")
    public ResponseEntity<Page<MusicWithoutMinistryResponse>> listAllMusicOfSomeMinistry(@PathVariable("ministry-id")
                                                                                         UUID ministryId,
                                                                                         @RequestParam(name = "page", defaultValue = "0") int page,
                                                                                         @RequestParam(name = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok().body(this.musicService.listMusicMinistryByMinistryId(ministryId, pageable));
    }
}
