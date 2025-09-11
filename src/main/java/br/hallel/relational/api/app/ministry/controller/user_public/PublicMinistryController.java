package br.hallel.relational.api.app.ministry.controller.user_public;

import br.hallel.relational.api.app.ministry.dto.MemberMinistryResponseWithFunctions;
import br.hallel.relational.api.app.ministry.dto.MinistryParticipationResponse;
import br.hallel.relational.api.app.ministry.dto.MinistryResponse;
import br.hallel.relational.api.app.ministry.service.MemberMinistryService;
import br.hallel.relational.api.app.ministry.service.MinistryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/public/ministry")
@RequiredArgsConstructor
@Tag(name = "Ministry public",
        description = "Public routes for ministry usage")
public class PublicMinistryController {

    private final MinistryService service;
    private final MemberMinistryService memberMinistryService;

    @GetMapping("/list-all")
    public ResponseEntity<Page<MinistryResponse>> litAllMinistries(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size

    ) {
        return ResponseEntity.ok(this.service.listAllMinistriesPage(page, size));
    }

    @GetMapping("/list-all/ministries")
    public ResponseEntity<List<MinistryResponse>> litAllMinistries() {
        return ResponseEntity.ok(this.service.listAllMinistries());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<MinistryResponse> getMinistryById(
            @PathVariable(name = "id") UUID id) {
        return ResponseEntity.ok(this.service.getMinistryById(id));
    }

    @GetMapping("/member-ministry/list/{ministry-id}")
    @Operation(summary = "List all members of ministry",
            description = "List all the members inserted in ministry by ministry identifier, you can paginate this request")
    public ResponseEntity<Page<MemberMinistryResponseWithFunctions>> listAllMembersMinistryByMinistryId(
            @PathVariable("ministry-id") UUID ministryId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10")
            int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return ResponseEntity.ok()
                .body(memberMinistryService.getAllMemberOfMinistry(ministryId, pageRequest));
    }

    @GetMapping("/ministry-user-participate/{user-id}")
    @Operation(summary = "List all ministry of user",
            description = "Route to list all the ministry that user participate just passing his id")
    public ResponseEntity<List<MinistryParticipationResponse>> listAllMinistryThatUserParticipate(
            @PathVariable("user-id") UUID userId) {
        return ResponseEntity.ok()
                .body(memberMinistryService.getMinistryThatUserParticipate(userId));
    }

}
