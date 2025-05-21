package br.hallel.relational.api.app.ministry.controller.user_public;


import br.hallel.relational.api.app.event.dto.MemberEventScaleResponseUserInfos;
import br.hallel.relational.api.app.event.service.MemberEventScaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequestMapping("/user/ministry/member-event-scale")
@RestController
@RequiredArgsConstructor
@Tag(name = "User - Member Event scale", description = "User part for member event in some scale information")
public class UserMemberEventScaleController {

    private final MemberEventScaleService memberEventScaleService;

    @PatchMapping("/accept-participation")
    @Operation(summary = "Accept participation of user in some scale", description = "Route to accept the participation of user in some scale passing the id of scale and id of user")
    public ResponseEntity<MemberEventScaleResponseUserInfos> acceptParticipationInScale(@RequestParam(name = "eventScaleId") UUID eventScaleId, @RequestParam(name = "userId") UUID userId) {
        return ResponseEntity.ok().body(this.memberEventScaleService.confirmParticipationUserInEvent(eventScaleId, userId));
    }

}
