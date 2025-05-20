package br.hallel.relational.api.app.ministry.controller.coordinator;

import br.hallel.relational.api.app.event.dto.MemberInvitedAndConfirmedResponse;
import br.hallel.relational.api.app.event.dto.MemberNotConfirmedResponse;
import br.hallel.relational.api.app.event.service.MemberEventScaleService;
import br.hallel.relational.api.app.ministry.dto.AddRemoveFunctionMinistryToMemberMinistryDTO;
import br.hallel.relational.api.app.ministry.dto.FunctionMinistryMemberResponse;
import br.hallel.relational.api.app.ministry.dto.MemberMinistryResponseWithFunctions;
import br.hallel.relational.api.app.ministry.model.MemberMinistry;
import br.hallel.relational.api.app.ministry.service.FunctionMinistryMemberService;
import br.hallel.relational.api.app.ministry.service.MemberMinistryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/coordinator/ministry/member-ministry")
@Tag(name = "Member Ministry - Coordinator",
     description = "Coordinator part for member ministry managment")
@RequiredArgsConstructor
public class CoordinatorMemberMinistryController {

    private final MemberMinistryService memberMinistryService;
    private final FunctionMinistryMemberService functionMinistryMemberService;
    private final MemberEventScaleService memberEventScaleService;
    @GetMapping("/list/{ministry-id}")
    @Operation(
            summary = "List all members of ministry",
            description = "List all the members inserted in ministry by ministry identifier, you can paginate this request"
    )
    public ResponseEntity<Page<MemberMinistryResponseWithFunctions>> listAllMembersMinistryByMinistryId(
            @PathVariable("ministry-id") UUID ministryId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10")
            int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return ResponseEntity.ok()
                             .body(memberMinistryService.getAllMemberOfMinistry(ministryId, pageRequest));
    }

    @PatchMapping("/add")
    @Operation(
            summary = "Adding member into ministry",
            description = "Route for adding member into ministry passing paramenters ministry id and user id"
    )
    public ResponseEntity<MemberMinistry> addMemberMinistryIntoMinistry(
            @RequestParam(name = "ministry-id") UUID ministryId,
            @RequestParam(name = "user-id") UUID userId) {
        return ResponseEntity.ok()
                             .body(memberMinistryService.addMemberIntoMinistry(ministryId, userId));
    }

    @DeleteMapping("/remove")
    @Operation(
            summary = "Remove member from ministry",
            description = "Route for remove member from ministry passing paramenters ministry id and user id"
    )
    public ResponseEntity<?> removeMemberMinistryOfMinistry(
            @RequestParam(name = "ministry-id") UUID ministryId,
            @RequestParam(name = "user-id") UUID userId) {
        memberMinistryService.removeMemberFromMinistry(ministryId, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/add/function")
    @Operation(
            summary = "Adding a function ministry to member ministry",
            description = "Route to associate a function of ministry to a member of this ministry, passing the id of the function and user id")
    public ResponseEntity<FunctionMinistryMemberResponse> addFunctionMinistryToAMember(
            @RequestBody
            AddRemoveFunctionMinistryToMemberMinistryDTO dto) {
        return ResponseEntity.ok()
                             .body(this.functionMinistryMemberService.associateAFunctionMinistryToMember(dto.getFunctionMinistryId(), dto.getUserId()));
    }

    @DeleteMapping("/remove/function")
    @Operation(
            summary = "Remove a function ministry of a member ministry",
            description = "Route to remove a function of ministry of a1 member of this ministry, passing the id of the function and user id")
    public ResponseEntity<?> removeFunctionMinistryToAMember(
            @RequestBody
            AddRemoveFunctionMinistryToMemberMinistryDTO dto) {
        this.functionMinistryMemberService.removeFunctionMinistryMember(dto.getFunctionMinistryId(), dto.getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/list-all/members-not-confirmed/{idScale}")
    public ResponseEntity<List<MemberNotConfirmedResponse>> listAllMembersThatNotConfirmed(@PathVariable(name = "idScale") UUID idScale) {
        return ResponseEntity.ok(this.memberEventScaleService.listNotConfirmedMembersEventScale(idScale));
    }

    @GetMapping("/list-all/members-confirmed/{idScale}")
    public ResponseEntity<List<MemberInvitedAndConfirmedResponse>> listAllMembersThatConfirmed(@PathVariable(name = "idScale") UUID idScale) {
        return ResponseEntity.ok(this.memberEventScaleService.listConfirmedMembersEventScale(idScale));
    }

    @GetMapping("/list-all/members-invited/{idScale}")
    public ResponseEntity<List<MemberInvitedAndConfirmedResponse>> listAllMembersThatInvited(@PathVariable(name = "idScale") UUID idScale) {
        return ResponseEntity.ok(this.memberEventScaleService.listInvitedMembersEventScale(idScale));
    }
    @GetMapping("/get/member-not-confirmed/{idScale}/{idUser}")
    public ResponseEntity<MemberNotConfirmedResponse> getMemberThatNotConfirmed(
            @PathVariable(name = "idScale") UUID idScale,
            @PathVariable(name = "idUser") UUID idUser
    ) {
        return ResponseEntity.ok(this.memberEventScaleService.getMemberReasonAbscence(idScale,idUser));
    }

}
