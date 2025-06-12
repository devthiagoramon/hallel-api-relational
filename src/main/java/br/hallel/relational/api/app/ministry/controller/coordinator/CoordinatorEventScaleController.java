package br.hallel.relational.api.app.ministry.controller.coordinator;

import br.hallel.relational.api.app.event.dto.*;
import br.hallel.relational.api.app.event.service.MemberEventScaleService;
import br.hallel.relational.api.app.event.service.EventScaleService;
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
@RequestMapping("/coordinator/event/scale")
@RequiredArgsConstructor
@Tag(name = "Coordinator - Event Scale", description = "Coordinator part for event scale managment")
public class CoordinatorEventScaleController {

    @Autowired
    private EventScaleService service;
    @Autowired
    private MemberEventScaleService memberEventScaleService;

    @GetMapping("/list/members-participate/{idMember}")
    public ResponseEntity<List<ScaleEventWithEventInfoResponse>> listEventsMembersParticipate(
            @PathVariable(name = "idMember") UUID idMember,
            @RequestBody EventScaleMemberCanParticipateDTO dto) {
        return ResponseEntity.ok(this.service.listScaleMinistryIdsByMemberIdThatCanParticipate(idMember, dto.start(), dto.end()));
    }

    @GetMapping("/list/confirmeds-members/{idMember}")
    public ResponseEntity<List<ScaleEventWithEventInfoResponse>> listScaleMinistryConfirmedMember(
            @PathVariable(name = "idMember") UUID idMember,
            @RequestBody EventScaleMemberCanParticipateDTO dto) {
        return ResponseEntity.ok(this.service.listScaleMinistryConfirmedMember(idMember, dto.start(), dto.end()));
    }

    @PostMapping("/add-remove/repertory/{idScale}")
    public ResponseEntity<ScaleEventResponseWithInfos> addAndRemoveRepertoryInScala(
            @PathVariable(name = "idScale") UUID idScale,
            @RequestBody ScaleRepertoryDTO dto) {
        return ResponseEntity.ok(this.service.addAndRemoveRepertoryInScale(idScale, dto));
    }

    @GetMapping("/get-event/{idEventScale}")
    public ResponseEntity<EventByEventScaleResponse> getEventByEventScaleId(
            @PathVariable(name = "idEventScale") UUID idEventScale) {
        return ResponseEntity.ok(this.service.getEventByEventScaleId(idEventScale));
    }

    @GetMapping("/list-all/member-status/{idScale}")
    public ResponseEntity<List<MemberEventScaleResponseUserInfos>> listAllMemberStatusByEventScaleId(
            @PathVariable(name = "idScale") UUID idScale) {
        return ResponseEntity.ok(this.memberEventScaleService.listAllMemberEventScaleStatus(idScale));
    }

    @PatchMapping("/accept-decline/member")
    public ResponseEntity<MemberEventScaleResponseUserInfos> acceptOrDeclineMemberScale(
            @RequestParam(name = "idMember") UUID idMember,
            @RequestParam(name = "eventScaleId") UUID eventScaleId,
            @RequestBody AcceptOrDeclineMemberInScale isAccept) {
        return ResponseEntity.ok(this.memberEventScaleService.acceptOrDeclineMember(idMember,eventScaleId, isAccept));
    }


}
