package br.hallel.relational.api.app.event.controller.user_public;

import br.hallel.relational.api.app.event.dto.EventParticipationDTO;
import br.hallel.relational.api.app.event.dto.EventParticipationResponse;
import br.hallel.relational.api.app.event.service.UserEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/public/event")
@RequiredArgsConstructor
@Tag(name = "Events", description = "Events User Participation part for public")
public class UserEventParticipationController {

    private final UserEventService userEventService;

    @Operation(summary = "Join an event")
    @PostMapping("/join")
    public ResponseEntity<EventParticipationResponse> joinEvent(@Valid @RequestBody EventParticipationDTO dto) {
        EventParticipationResponse response = userEventService.joinTheEvent(dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Leave an event participation by participation ID")
    @DeleteMapping("/leave/{participationId}")
    public ResponseEntity<Void> leaveEvent(@PathVariable UUID participationId) {
        userEventService.leaveTheEvent(participationId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get participation by ID")
    @GetMapping("/participation/{participationId}")
    public ResponseEntity<EventParticipationResponse> getParticipation(@PathVariable UUID participationId) {
        EventParticipationResponse response = userEventService.getParticipationById(participationId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "List all participations")
    @GetMapping("/participation")
    public ResponseEntity<List<EventParticipationResponse>> getAllParticipations() {
        List<EventParticipationResponse> responses = userEventService.getAllParticipations();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Edit event participation")
    @PutMapping("/edit/participation/{participationId}")
    public ResponseEntity<EventParticipationResponse> editParticipation(
            @PathVariable UUID participationId,
            @Valid @RequestBody EventParticipationDTO dto) {
        EventParticipationResponse response = userEventService.editParticipationEvent(participationId, dto);
        return ResponseEntity.ok(response);
    }

}
