package br.hallel.relational.api.app.ministry.controller.user_public;


import br.hallel.relational.api.app.event.dto.EventScaleResponse;
import br.hallel.relational.api.app.event.dto.EventScaleWithStatusInfos;
import br.hallel.relational.api.app.event.dto.MemberEventScaleResponseUserInfos;
import br.hallel.relational.api.app.event.model.EventScale;
import br.hallel.relational.api.app.event.service.MemberEventScaleService;
import br.hallel.relational.api.app.global.utils.DateUtils;
import br.hallel.relational.api.app.ministry.dto.ReasonAbscenceUserDTO;
import br.hallel.relational.api.app.ministry.service.MinistryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequestMapping("/user/ministry/member-event-scale")
@RestController
@RequiredArgsConstructor
@Tag(name = "User - Member Event scale", description = "User part for member event in some scale information")
public class UserMemberEventScaleController {

    private final MemberEventScaleService memberEventScaleService;
    private final MinistryService ministryService;

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
    public ResponseEntity<List<EventScaleResponse>> listAllScalesByMinistryIdAndRangeDate(
            @PathVariable(name = "idMinistry") UUID idMinistry,
            @RequestParam(name = "start") LocalDateTime start,
            @RequestParam(name = "end") LocalDateTime end) {
        return ResponseEntity.ok(this.ministryService.listAllEventScalesByMinistryIdAndRangeDate(idMinistry,
                start, end));
    }

    @GetMapping("/scales-range-date-with-status/{idMinistry}/{userId}")
    @Operation(summary = "List all scales of user in ministry with status", description = "List all scales of user in some ministry with status in range of date")
    public ResponseEntity<List<EventScaleWithStatusInfos>> listAllScaleOfUserWithStatusInfoByRangeDate(
            @PathVariable(name = "idMinistry") UUID ministryId,
            @PathVariable(name = "userId") UUID userId,
            @RequestParam(name = "start") LocalDateTime start,
            @RequestParam(name = "end") LocalDateTime end) {
        return ResponseEntity.ok(
                this.memberEventScaleService.listAllScaleOfUserInMinistryInRangeOfDateStatus(userId, ministryId,
                        DateUtils.convertLocalDateTimeToDate(start), DateUtils.convertLocalDateTimeToDate(end)));
    }

}
