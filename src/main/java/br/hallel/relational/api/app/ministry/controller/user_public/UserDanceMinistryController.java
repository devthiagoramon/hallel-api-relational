package br.hallel.relational.api.app.ministry.controller.user_public;

import br.hallel.relational.api.app.ministry.dto.DanceResponseShort;
import br.hallel.relational.api.app.ministry.service.DanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user/ministry/dance")
@RequiredArgsConstructor
@Tag(name = "Member Dance Ministry", description = "Member part for dance ministry infos")
public class UserDanceMinistryController {

    private DanceService danceService;

    @GetMapping("/{ministry-id}")
    @Operation(summary = "Listing dances of ministry", description = "Route to list all the dances of ministry by ministry id")
    public ResponseEntity<Page<DanceResponseShort>> listAllDancesOfMinistry(@PathVariable(name = "ministry-id")
                                                                            UUID ministryId, @RequestParam(name = "page") int page, @RequestParam(name = "size") int size){
        return ResponseEntity.ok().body(this.danceService.listDancesOfMinistry(ministryId, PageRequest.of(page, size)));
    }

}
