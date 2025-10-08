package br.hallel.relational.api.app.ministry.controller.user_public;

import br.hallel.relational.api.app.event.dto.EventScaleWithInfos;
import br.hallel.relational.api.app.event.service.EventScaleService;
import br.hallel.relational.api.app.ministry.model.MemberMinistry;
import br.hallel.relational.api.app.ministry.service.MemberMinistryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/user/member-ministry/")
@RequiredArgsConstructor
@Tag(name = "User member ministry", description = "User part for member ministry funcionalities")
public class UserMemberMinistryController {

    private final MemberMinistryService memberMinistryService;
    private final EventScaleService eventScaleService;

    @GetMapping("/{userId}/{ministryId}")
    @Operation(summary = "Get member ministry by user id and ministry id", description = "Route to get the member ministry register with user id and ministry id")
    public ResponseEntity<MemberMinistry> listMemberMinistryByMinistryAndUserId(@PathVariable(name = "ministryId") UUID ministryId, @PathVariable(name = "userId") UUID userId) {
        return ResponseEntity.ok(this.memberMinistryService.getMemberMinistryByUserAndMinistryId(userId, ministryId));
    }

    @GetMapping("/get-scale/{scaleId}")
    @Operation(summary = "Get all scales infos", description = "Handles getting info of some scale passing his id and user id")
    public ResponseEntity<EventScaleWithInfos> getScaleById(
            @PathVariable(name = "scaleId") UUID eventScaleId) {
        EventScaleWithInfos response = eventScaleService.getEventScaleWithInfos(eventScaleId);
        return ResponseEntity.ok(response);
    }

}
