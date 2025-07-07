package br.hallel.relational.api.app.ministry.controller.user_public;


import br.hallel.relational.api.app.event.dto.*;
import br.hallel.relational.api.app.event.model.EventScale;
import br.hallel.relational.api.app.event.service.EventService;
import br.hallel.relational.api.app.event.service.MemberEventScaleService;
import br.hallel.relational.api.app.global.utils.DateUtils;
import br.hallel.relational.api.app.ministry.dto.EventScaleSimpleResponse;
import br.hallel.relational.api.app.ministry.dto.ReasonAbscenceUserDTO;
import br.hallel.relational.api.app.ministry.dto.RepertoryResponse;
import br.hallel.relational.api.app.ministry.service.MinistryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequestMapping("/user/ministry/member-event-scale")
@RestController
@RequiredArgsConstructor
@Tag(name = "User - Member Event scale", description = "User part for member event in some scale information")
public class UserMemberEventScaleController {

    private final MemberEventScaleService memberEventScaleService;
    private final MinistryService ministryService;
    private final EventService eventService;

    @PatchMapping("/accept-participation")
    @Operation(summary = "Accept participation of user in some scale", description = "Route to accept the participation of user in some scale passing the id of scale and id of user")
    public ResponseEntity<MemberEventScaleResponseUserInfos> acceptParticipationInScale(
            @RequestParam(name = "eventScaleId") UUID eventScaleId, @RequestParam(name = "userId") UUID userId) {
        return ResponseEntity.ok()
                .body(this.memberEventScaleService.confirmParticipationUserInEvent(eventScaleId, userId));
    }

    @PatchMapping("/decline-participation")
    @Operation(summary = "Decline participation of user in some scale", description = "Route to decline participation fo some user in scale passing the id of scale and id of user")
    public ResponseEntity<MemberEventScaleResponseUserInfos> declineParticipationInScale(
            @RequestParam(name = "eventScaleId") UUID eventScaleId, @RequestParam(name = "userId") UUID userId,
            @RequestBody ReasonAbscenceUserDTO reasonAbscenceUserDTO) {
        return ResponseEntity.ok()
                .body(this.memberEventScaleService.declineParticipationUserInEvent(eventScaleId, userId,
                        reasonAbscenceUserDTO.getReason()));
    }

    @GetMapping("/invited-scales")
    @Operation(summary = "List scales whos user has been invited", description = "Route to list scales who's user has been invited in range date, passing the user id, ministry id, initial and final date")
    public ResponseEntity<List<EventScale>> listEventScalesOfUserInvitedBetweenRangeDate(
            @RequestParam("userId") UUID userId,
            @RequestParam("ministryId") UUID ministryId,
            @RequestParam("initial")
            LocalDateTime initialDate,
            @RequestParam("final") LocalDateTime finalDate) {
        return ResponseEntity.ok()
                .body(this.memberEventScaleService.listAllInvitedScaleOfUserInMinistryInRangeOfDate(userId, ministryId,
                        DateUtils.convertLocalDateTimeToDate(initialDate),
                        DateUtils.convertLocalDateTimeToDate(finalDate)));
    }


    @GetMapping("/scales-range-date/{idMinistry}")
    @Operation(
            summary = "Listing all Events Scales By Ministry ID and Range Date (start date, end date)"
    )
    public ResponseEntity<List<EventScaleSimpleResponse>> listAllScalesByMinistryIdAndRangeDate(
            @PathVariable(name = "idMinistry") UUID idMinistry,
            @RequestParam(name = "start") LocalDateTime start,
            @RequestParam(name = "end") LocalDateTime end) {
        return ResponseEntity.ok(this.ministryService.listAllEventScalesByMinistryIdAndRangeDate(idMinistry,
                start, end));
    }

    @GetMapping("/scales-range-date-with-status/{idMinistry}/{memberMinistryId}")
    @Operation(summary = "List all scales of user in ministry with status", description = "List all scales of user in some ministry with status in range of date")
    public ResponseEntity<List<EventScaleWithStatusInfos>> listAllScaleOfUserWithStatusInfoByRangeDate(
            @PathVariable(name = "idMinistry") UUID ministryId,
            @PathVariable(name = "memberMinistryId") UUID memberMinistryId,
            @RequestParam(name = "start") LocalDateTime start,
            @RequestParam(name = "end") LocalDateTime end) {
        return ResponseEntity.ok(
                this.memberEventScaleService.listAllScaleOfUserInMinistryInRangeOfDateStatus(memberMinistryId, ministryId, DateUtils.convertLocalDateTimeToDate(start), DateUtils.convertLocalDateTimeToDate(end)));
    }

    @GetMapping("/scales-range-date-with-members/{idMinistry}/{memberMinistryId}")
    @Operation(summary = "List all scales of user in ministry with members", description = "List all scales of user in some ministry with status and membersin range of date")
    public ResponseEntity<List<EventScaleWithMembers>> listAllEventScaleWithMembers(
            @PathVariable(name = "idMinistry") UUID ministryId,
            @PathVariable(name = "memberMinistryId") UUID memberMinistryId,
            @RequestParam(name = "start") LocalDateTime start,
            @RequestParam(name = "end") LocalDateTime end) {
        return ResponseEntity.ok(
                this.memberEventScaleService.listAllEventScaleWithMembers(memberMinistryId, ministryId, DateUtils.convertLocalDateTimeToDate(start), DateUtils.convertLocalDateTimeToDate(end)));
    }

    @GetMapping("/get-status/{eventScaleId}/{memberMinistryId}")
    @Operation(
            summary = "Getting Member Status in Event Scale by your Id"
    )
    public ResponseEntity<MemberAuditionStatusResponse> getMemberStatus(
            @PathVariable(name = "memberMinistryId") UUID memberMinistryId,
            @PathVariable(name = "eventScaleId") UUID eventScaleId
    ) {

        MemberAuditionStatusResponse status = this.memberEventScaleService.getMemberStatus(memberMinistryId, eventScaleId);
        log.info(status.getAuditionStatus());
        return ResponseEntity.ok(status);
    }


    @GetMapping("/list-repertory/{event-scale-id}")
    @Operation(summary = "Listing repertories of some event scale", description = "Route for listing the repertories of some event scale just passing the event scale id")
    public ResponseEntity<List<RepertoryResponse>> listRepertoryByEventScaleId(@PathVariable(name = "event-scale-id") UUID eventScaleId) {
        return ResponseEntity.ok(this.memberEventScaleService.listAllRepertoryOfEventScale(eventScaleId));
    }

    @GetMapping("/scale/view-invite")
    @Operation(
            summary = "the system records whether or not the member viewed the invitation"
    )
    public ResponseEntity<Boolean> viewInviteByEventScaleId(
            @RequestParam(name = "idEventScale") UUID eventScaleId,
            @RequestParam(name = "idUser") UUID userId
    ) {
        return ResponseEntity.ok(this.memberEventScaleService.viewInvite(eventScaleId, userId));
    }

    @GetMapping("/get-events/ministry-id/{ministry-id}")
    @Operation(summary = "Get events by ministry ID")
    public ResponseEntity<List<EventSimpleResponse>> listAllEventsInMinistry(
            @PathVariable(name = "ministry-id") UUID ministryId
    ) {
        return ResponseEntity.ok(this.eventService.listAllEventsByMinistryId(ministryId));
    }
}
