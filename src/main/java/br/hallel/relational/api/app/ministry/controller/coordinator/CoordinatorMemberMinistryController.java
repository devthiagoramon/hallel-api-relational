package br.hallel.relational.api.app.ministry.controller.coordinator;

import br.hallel.relational.api.app.ministry.model.MemberMinistry;
import br.hallel.relational.api.app.ministry.service.MemberMinistryService;
import br.hallel.relational.api.app.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/coordinator/ministry/member-ministry")
@Tag(name = "Member Ministry - Coordinator", description = "Coordinator part for member ministry managment")
@RequiredArgsConstructor
public class CoordinatorMemberMinistryController {

    private final MemberMinistryService memberMinistryService;

    @GetMapping("/list/{ministry-id}")
    @Operation(
            summary = "List all members of ministry",
            description = "List all the members inserted in ministry by ministry identifier, you can paginate this request"
    )
    public ResponseEntity<Page<User>> listAllMembersMinistryByMinistryId(@PathVariable("ministry-id") UUID ministryId, @RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "size", defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return ResponseEntity.ok().body(memberMinistryService.getAllMemberOfMinistry(ministryId, pageRequest));
    }

    @PatchMapping("/add")
    @Operation(
            summary = "Adding member into ministry",
            description = "Route for adding member into ministry passing paramenters ministry id and user id"
    )
    public ResponseEntity<MemberMinistry> addMemberMinistryIntoMinistry(@RequestParam(name = "ministry-id") UUID ministryId, @RequestParam(name = "user-id") UUID userId) {
        return ResponseEntity.ok().body(memberMinistryService.addMemberIntoMinistry(ministryId, userId));
    }

    @DeleteMapping("/remove")
    @Operation(
            summary = "Remove member from ministry",
            description = "Route for remove member from ministry passing paramenters ministry id and user id"
    )
    public ResponseEntity<?> removeMemberMinistryOfMinistry(@RequestParam(name = "ministry-id") UUID ministryId, @RequestParam(name = "user-id") UUID userId) {
        memberMinistryService.removeMemberFromMinistry(ministryId, userId);
        return ResponseEntity.noContent().build();
    }

}
