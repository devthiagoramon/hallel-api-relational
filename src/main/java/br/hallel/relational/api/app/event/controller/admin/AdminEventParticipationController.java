package br.hallel.relational.api.app.event.controller.admin;

import br.hallel.relational.api.app.event.dto.EventParticipationResponse;
import br.hallel.relational.api.app.event.model.UserFunctionInEvent;
import br.hallel.relational.api.app.event.service.UserEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/public/event")
@RequiredArgsConstructor
@Tag(name = "Events", description = "Events User Participation part for Admin")
public class AdminEventParticipationController {

    private final UserEventService userEventService;

    @Operation(summary = "Edit User Function participation")
    @PatchMapping("/participation/edit-function/{participationId}")
    public ResponseEntity<EventParticipationResponse> editUserFunction(
            @PathVariable UUID participationId,
            @Valid @RequestParam UserFunctionInEvent userFunctionInEvent) {
        EventParticipationResponse response = userEventService.addFunctionUserInEvent(participationId, userFunctionInEvent);
        return ResponseEntity.ok(response);
    }
}
