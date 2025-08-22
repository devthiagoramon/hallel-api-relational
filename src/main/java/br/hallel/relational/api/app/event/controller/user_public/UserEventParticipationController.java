package br.hallel.relational.api.app.event.controller.user_public;

import br.hallel.relational.api.app.event.dto.*;
import br.hallel.relational.api.app.event.model.EventParticipation;
import br.hallel.relational.api.app.event.model.StatusPaymentEventParticipation;
import br.hallel.relational.api.app.event.service.UserEventService;
import br.hallel.relational.api.app.security.utils.JwtTokenProvider;
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
@RequestMapping("/user/event")
@RequiredArgsConstructor
@Tag(name = "Events - User participation", description = "Events User Participation part for public")
public class UserEventParticipationController {

    private final UserEventService userEventService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "Join an event")
    @PostMapping("/join")
    public ResponseEntity<EventParticipationResponse> joinEvent(
            @RequestHeader("Authorization") String authorizationHeader, @RequestBody EventParticipateDTO dto) {

        UUID userId = jwtTokenProvider.getUserId(authorizationHeader);
        EventParticipationResponse response = userEventService.joinTheEvent(userId, dto);
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

    @GetMapping("/participation/status")
    @Operation(summary = "List user participation status in event")
    public ResponseEntity<UserEventStatus> getParticipationStatus(
            @RequestHeader("Authorization") String authorizationHeader, @RequestParam(name = "eventId") UUID eventId) {
        UUID userId = jwtTokenProvider.getUserId(authorizationHeader);
        return ResponseEntity.ok(this.userEventService.getStatusParticipationOfEvent(userId, eventId));
    }

    @GetMapping("/participation/list-all/by-payment-status")
    @Operation(summary = "List user participation payment status in event")
    public ResponseEntity<List<UserEventStatus>> getPaymentStatus(
            @RequestParam(name = "eventId") UUID eventId,
            @RequestParam(name = "paymentStatus")StatusPaymentEventParticipation status) {
        return ResponseEntity.ok(this.userEventService.getStatusPayementParticipationOfEvent(eventId, status));
    }

    @GetMapping("/participation/object")
    @Operation(summary = "Get user participation object")
    public ResponseEntity<EventParticipation> getParticipationObject(
            @RequestHeader("Authorization") String authorizationHeader, @RequestParam(name = "eventId") UUID eventId) {
        UUID userId = jwtTokenProvider.getUserId(authorizationHeader);
        return ResponseEntity.ok(userEventService.getUserParticipationInEventByUserId(userId, eventId));

    }


}