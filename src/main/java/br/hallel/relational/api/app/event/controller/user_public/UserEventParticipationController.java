package br.hallel.relational.api.app.event.controller.user_public;

import br.hallel.relational.api.app.event.dto.*;
import br.hallel.relational.api.app.event.model.EventParticipation;
import br.hallel.relational.api.app.event.model.enum_type.StatusPaymentEventParticipation;
import br.hallel.relational.api.app.event.service.UserEventService;
import br.hallel.relational.api.app.security.utils.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/user/event/participation")
@RequiredArgsConstructor
@Tag(name = "Events - User participation", description = "Events User Participation part ")
public class UserEventParticipationController {

    private final UserEventService userEventService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/pay/{eventId}")
    @Operation(summary = "Pay an Event (or retreat)")
    public ResponseEntity<EventPayParticipationDetails> payAnEvent(
            @RequestHeader("Authorization") String authorizationHeader, @PathVariable(name = "eventId") UUID eventId) {

        UUID userId = jwtTokenProvider.getUserId(authorizationHeader);
        return ResponseEntity.ok(userEventService.payAnEvent(userId, eventId));
    }

    @Operation(summary = "Leave an event participation by participation ID")
    @DeleteMapping("/leave/{eventId}")
    public ResponseEntity<Void> leaveEvent(@RequestHeader("Authorization") String authorizationHeader,
                                           @PathVariable(name = "eventId") UUID eventId) {

        UUID userId = jwtTokenProvider.getUserId(authorizationHeader);
        userEventService.leaveTheEventAsUser(eventId, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get participation by ID")
    @GetMapping("/get/{eventId}")
    public ResponseEntity<EventParticipationResponse> getParticipation(
            @RequestHeader("Authorization") String authorization,
            @PathVariable(name = "eventId") UUID eventId
    ) {
        EventParticipationResponse response = userEventService.getParticipationById(
                jwtTokenProvider.getUserId(authorization), eventId
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "List all participations in all Events")
    @GetMapping("")
    public ResponseEntity<ParticipationListResponse> getAllParticipations(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        Page<EventParticipationResponse> responses = userEventService.getAllParticipations(page, size);
        return ResponseEntity.ok(
                new ParticipationListResponse(responses, responses.getSize())
        );
    }

    @Operation(summary = "List all participations by User Id")
    @GetMapping("/by-user")
    public ResponseEntity<Page<UserInEventWithEventInfosResponse>>
    getAllParticipationsByUserId(@RequestHeader("Authorization") String authorizationHeader,
                                 @RequestParam(name = "page", defaultValue = "0") int page,
                                 @RequestParam(name = "size", defaultValue = "10") int size) {

        return ResponseEntity.ok(userEventService.
                getAllUserParticipationByUserId(jwtTokenProvider.getUserId(authorizationHeader), PageRequest.of(page, size)));
    }

    @GetMapping("/status")
    @Operation(summary = "List user participation status in event")
    public ResponseEntity<UserEventStatus> getParticipationStatus(
            @RequestHeader("Authorization") String authorizationHeader, @RequestParam(name = "eventId") UUID eventId) {
        UUID userId = jwtTokenProvider.getUserId(authorizationHeader);
        return ResponseEntity.ok(this.userEventService.getStatusParticipationOfEvent(userId, eventId));
    }

    @GetMapping("/list-all/by-payment-status")
    @Operation(summary = "List user participation by payment status in event")
    public ResponseEntity<Page<UserEventStatus>> getPaymentStatus(
            @RequestParam(name = "eventId") UUID eventId,
            @RequestParam(name = "paymentStatus") StatusPaymentEventParticipation status,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "0") int size
            ){
        return ResponseEntity.ok(this.userEventService.getStatusPayementParticipationOfEvent(eventId, status,
                PageRequest.of(page, size)));
    }

    @GetMapping("/object")
    @Operation(summary = "Get user participation object")
    public ResponseEntity<EventParticipation> getParticipationObject(
            @RequestHeader("Authorization") String authorizationHeader, @RequestParam(name = "eventId") UUID eventId) {
        UUID userId = jwtTokenProvider.getUserId(authorizationHeader);
        return ResponseEntity.ok(userEventService.getUserParticipationInEventByUserId(userId, eventId));

    }

    @GetMapping("/payment-detail/{eventId}")
    @Operation(summary = "Get user participation object")
    public ResponseEntity<UserPaymentDetailResponse> getDetailsOfPayment(
            @RequestHeader("Authorization") String authorizationHeader, @PathVariable(name = "eventId") UUID eventId) {
        UUID userId = jwtTokenProvider.getUserId(authorizationHeader);
        return ResponseEntity.ok(this.userEventService.getUserPaymentDetail(userId, eventId));

    }

}