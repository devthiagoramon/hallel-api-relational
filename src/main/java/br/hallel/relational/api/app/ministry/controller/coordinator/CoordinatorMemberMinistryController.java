package br.hallel.relational.api.app.ministry.controller.coordinator;

import br.hallel.relational.api.app.event.dto.*;
import br.hallel.relational.api.app.event.exception.MemberEventScaleNotFoundException;
import br.hallel.relational.api.app.event.service.EventService;
import br.hallel.relational.api.app.event.service.MemberEventScaleService;
import br.hallel.relational.api.app.event.service.EventScaleService;
import br.hallel.relational.api.app.ministry.dto.*;
import br.hallel.relational.api.app.ministry.model.MemberMinistry;
import br.hallel.relational.api.app.ministry.service.FunctionMinistryMemberService;
import br.hallel.relational.api.app.ministry.service.MemberMinistryService;
import br.hallel.relational.api.app.user.dto.UserShortResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/coordinator/ministry/member-ministry")
@Tag(name = "Member Ministry - Coordinator",
        description = "Coordinator part for member ministry managment")
@RequiredArgsConstructor
public class CoordinatorMemberMinistryController {

    private final MemberMinistryService memberMinistryService;
    private final FunctionMinistryMemberService functionMinistryMemberService;
    private final MemberEventScaleService memberEventScaleService;
    private final EventScaleService eventScaleService;
    private final EventService eventService;

    @GetMapping("/list/{ministry-id}")
    @Operation(
            summary = "List all members of ministry",
            description = "List all the members inserted in ministry by ministry identifier, you can paginate this request"
    )
    public ResponseEntity<Page<MemberMinistryResponseWithFunctions>> listAllMembersMinistryByMinistryId(
            @PathVariable("ministry-id") UUID ministryId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return ResponseEntity.ok()
                .body(memberMinistryService.getAllMemberOfMinistry(ministryId, pageRequest));
    }

    @GetMapping("/addable/{ministry-id}")
    @Operation(summary = "List all users addable to ministry",
            description = "Route to list all users in system that can be addable in ministry passed as ministry-id")
    public ResponseEntity<Page<UserShortResponse>> listUserAddableToMinistryByMinistryId(
            @PathVariable(name = "ministry-id") UUID ministryId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20")
            Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return ResponseEntity.ok().body(memberMinistryService.getUsersAddableInMinistry(ministryId, pageRequest));
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
    public ResponseEntity<List<MemberNotConfirmedResponse>> listAllMembersThatNotConfirmed(
            @PathVariable(name = "idScale") UUID idScale) {
        return ResponseEntity.ok(this.memberEventScaleService.listNotConfirmedMembersEventScale(idScale));
    }

    @GetMapping("/list-all/members-confirmed/{idScale}")
    public ResponseEntity<List<MemberInvitedAndConfirmedResponse>> listAllMembersThatConfirmed(
            @PathVariable(name = "idScale") UUID idScale) {
        return ResponseEntity.ok(this.memberEventScaleService.listConfirmedMembersEventScale(idScale));
    }

    @GetMapping("/list-all/members-invited/{idScale}")
    public ResponseEntity<List<MemberInvitedAndConfirmedResponse>> listAllMembersThatInvited(
            @PathVariable(name = "idScale") UUID idScale) {

        return ResponseEntity.ok(this.memberEventScaleService.listInvitedMembersEventScale(idScale));
    }

    @GetMapping("/get/member-not-confirmed/{idScale}/{idUser}")
    public ResponseEntity<MemberNotConfirmedResponse> getMemberThatNotConfirmed(
            @PathVariable(name = "idScale") UUID idScale,
            @PathVariable(name = "idUser") UUID idUser
    ) {
        return ResponseEntity.ok(this.memberEventScaleService.getMemberReasonAbscence(idScale, idUser));
    }

    @GetMapping("/list-all/by-status/confirmed/{idMemberMinistry}")
    public ResponseEntity<List<EventScaleSimpleResponse>>
    listEventsScalesByUserIdParticipate(
            @PathVariable(name = "idMemberMinistry") UUID idMemberMinistry,
            @RequestParam("dateStart") LocalDateTime dateStart,
            @RequestParam("dateEnd") LocalDateTime dateEnd) {
        return ResponseEntity.ok(this.eventScaleService.
                listEventsScalesByUserIdParticipate(idMemberMinistry, dateStart, dateEnd));
    }

    @GetMapping("/scale/event/{eventId}")
    public ResponseEntity<EventShortResponse>
    listEventInScaleInfo(@PathVariable(name = "eventId") UUID eventId) {
        return ResponseEntity.ok(this.eventService.listEventInScaleInfo(eventId));
    }

    @GetMapping("/get-scale/{id}/{userId}")
    public ResponseEntity<EventScaleWithInfos> getScaleById(
            @PathVariable(name = "id") UUID eventScaleId,
            @PathVariable(name = "userId") UUID userId) {

        EventScaleWithInfos response = eventScaleService.getEventScaleWithInfos(eventScaleId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/scale/list/members-can-participate/{scaleId}")
    public ResponseEntity<List<String>> listEventsMemberCanParticipate(
            @PathVariable(name = "scaleId") UUID scaleId
//            @RequestParam(name = "page", defaultValue = "0") int page,
//            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        log.info("listEventsMemberCanParticipate {}", scaleId);
        return ResponseEntity.ok(this.eventScaleService.listMembroMinisterioCanInviteToEscala(scaleId
//                , page, size
        ));
    }

    @PatchMapping("/scale/invite-members/{scaleId}")
    @Operation(
            summary = "Invite members to scale by their ids")
    public ResponseEntity<EventScaleSimpleResponse> inviteMembersToEventScale(
            @PathVariable("scaleId") UUID idEscala,
            @RequestBody List<UUID> membersIds) {
        return ResponseEntity.ok()
                .body(this.memberEventScaleService.inviteUserIntoScale(idEscala, membersIds));
    }

    @PatchMapping("/scale/withdraw-invitation/{scaleId}")
    @Operation(
            summary = "Withdraw invitation member to scale by your id")
    public ResponseEntity<EventScaleSimpleResponse> withdrawInvitationMembersToEventScale(
            @PathVariable("scaleId") UUID idEscala,
            @RequestBody List<UUID> membersIds) {

        return ResponseEntity.ok()
                .body(this.memberEventScaleService.withdrawInvitation(idEscala, membersIds));
    }

    @GetMapping("/guest/list-all/guests-invited/{id}")
    public ResponseEntity<List<GuestInvitedEventScaleResponse>> listAllGuestsInvitedEventScale(
            @PathVariable(name = "id") UUID id) {
        return ResponseEntity.ok(this.memberEventScaleService.listAllGuestsInvitedsByEventScaleId(id));
    }

    @DeleteMapping("/guest/remove/guests-invited/{id}")
    @Operation(summary = "Remove Guest invited in event scale",
            description = "Remove Guest invited in event scale by your id and send menssage to registred number",
            responses = {@ApiResponse(responseCode = "200",
                    description = "Guest Removed",
                    content = @Content(
                            schema = @Schema(
                                    implementation = Boolean.class))),
                    @ApiResponse(responseCode = "404",
                            description = "Guest not found",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = MemberEventScaleNotFoundException.class)))})
    public ResponseEntity<Boolean> removeGuestInvitedInEventScale(
            @PathVariable(name = "id") UUID id) {
        return ResponseEntity.ok(this.memberEventScaleService.removeGuestFromEventScaleById(id));
    }

    @PostMapping("/guest/create-invite")
    public ResponseEntity<GuestInvitedEventScaleResponse> createInviteInEventScaleId(
            @RequestBody GuestInvitedEventScaleDTO dto) {
        return ResponseEntity.ok(this.memberEventScaleService.createGuestInvitedEventScale(dto));
    }


    @GetMapping("/guest/get-invite/{inviteId}")
    public ResponseEntity<InviteEventScaleResponse> getInvitesInEventScaleId(
            @PathVariable(name = "inviteId") UUID inviteId) {
        return ResponseEntity.ok(this.memberEventScaleService.getInvitesInEventScaleId(inviteId));
    }

    @PutMapping("/guest/edit-invite")
    public ResponseEntity<InviteEventScaleResponse> editInvitesInEventScaleId(
            @RequestParam(name = "inviteId") UUID inviteId,
            @RequestParam(name = "guestId") UUID guestId,
            @RequestBody InviteEventScaleDTO dto) {
        return ResponseEntity.ok(this.memberEventScaleService.editInvitesInEventScaleId(inviteId,
                guestId, dto.message()));
    }

    @GetMapping("/guest/get-guest/{guestId}")
    @Operation(summary = "Get guest in event scale by your id")
    public ResponseEntity<GuestInvitedEventScaleResponse> getGuestInEventScaleId(
            @PathVariable(name = "guestId") UUID guestId) {
        return ResponseEntity.ok(this.memberEventScaleService.getGuestInvitedEventScale(guestId));
    }

    @PutMapping("/guest/edit-guest-infos/{guestId}")
    public ResponseEntity<GuestInvitedEventScaleResponse> editGuestInEventScaleId(
            @PathVariable(name = "guestId") UUID guestId,
            @RequestBody GuestInvitedEventScaleDTO dto) {
        return ResponseEntity.ok(this.memberEventScaleService.editGuestInvited(
                guestId, dto));
    }


}
