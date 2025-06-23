package br.hallel.relational.api.app.ministry.controller.user_public;

import br.hallel.relational.api.app.ministry.dto.RepertoryShortResponse;
import br.hallel.relational.api.app.ministry.service.RepertoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequestMapping("/user/ministry/repertory")
@RestController
@RequiredArgsConstructor
@Tag(name = "Member Music Ministry", description = "Member part for music ministry infos")
public class UserRepertoryMinistryController {

    private final RepertoryService service;

    @GetMapping("/list-all/ministry-id/{ministryId}")
    public ResponseEntity<List<RepertoryShortResponse>> listAllRepertoriesByMinistryId(
            @PathVariable("ministryId") UUID ministryId
                                                                                      ) {
        return ResponseEntity.ok(this.service.listAllRepertoryByMinistryId(ministryId));
    }
}
