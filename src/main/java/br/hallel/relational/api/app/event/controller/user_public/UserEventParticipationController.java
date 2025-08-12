package br.hallel.relational.api.app.event.controller.user_public;

import br.hallel.relational.api.app.event.dto.EventParticipationDTO;
import br.hallel.relational.api.app.event.dto.EventParticipationResponse;
import br.hallel.relational.api.app.event.dto.ParticipationListResponse;
import br.hallel.relational.api.app.event.dto.UserInEventInfosResponse;
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

    @Operation(summary = "List all participations in all Events")
    @GetMapping("/participations")
    public ResponseEntity<ParticipationListResponse> getAllParticipations() {
        List<EventParticipationResponse> responses = userEventService.getAllParticipations();
        return ResponseEntity.ok(
                new ParticipationListResponse(responses, responses.size())
        );
    }

    @Operation(summary = "List all participations by Event Id")
    @GetMapping("/participation/by-event/{eventId}")
    public ResponseEntity<List<UserInEventInfosResponse>>
    getAllParticipationsByEventId(@PathVariable(name = "eventId") UUID eventId) {
        List<UserInEventInfosResponse> responses = userEventService.getAllParticipationsByEventId(eventId);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "List all participations by User Id")
    @GetMapping("/participation/by-user/{userId}")
    public ResponseEntity<List<UserInEventInfosResponse>>
    getAllParticipationsByUserId(@PathVariable(name = "userId") UUID eventId) {
        List<UserInEventInfosResponse> responses = userEventService.getAllUserParticipationByUserId(eventId);
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