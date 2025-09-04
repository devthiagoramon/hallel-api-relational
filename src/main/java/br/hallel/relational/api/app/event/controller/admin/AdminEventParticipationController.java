package br.hallel.relational.api.app.event.controller.admin;

import br.hallel.relational.api.app.event.dto.*;
import br.hallel.relational.api.app.event.model.EventParticipation;
import br.hallel.relational.api.app.event.model.UserFunctionInEvent;
import br.hallel.relational.api.app.event.service.UserEventService;

import br.hallel.relational.api.app.security.utils.JwtTokenProvider;
import br.hallel.relational.api.app.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/admin/event/participation")
@RequiredArgsConstructor
@Tag(name = "Admin events - User participation", description = "Events User Participation part for Admin")
public class AdminEventParticipationController {

    private final UserEventService userEventService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "Edit User Function participation")
    @PatchMapping("/edit-function")
    public ResponseEntity<EventParticipationResponse> editUserFunction(
            @RequestParam(name = "eventId") UUID eventId,
            @RequestParam(name = "userId") UUID userId,
            @Valid @RequestParam UserFunctionInEvent userFunctionInEvent) {
        EventParticipationResponse response = userEventService.addFunctionUserInEvent(userId, eventId,
                userFunctionInEvent);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Edit event participation")
    @PutMapping("/edit/{participationId}")
    public ResponseEntity<EventParticipationResponse> editParticipation(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody EventParticipationDTO dto) {
        EventParticipationResponse response = userEventService.editParticipationEvent(
                jwtTokenProvider.getUserId(authorizationHeader), dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "List all participants by Event Id")
    @GetMapping("/by-event/{eventId}")
    public ResponseEntity<Page<UserInEventInfosResponse>>
    getAllParticipationsByEventId(@PathVariable(name = "eventId") UUID eventId,
                                  @RequestParam(name = "page", defaultValue = "0") int page,
                                  @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(userEventService.getAllParticipationsByEventId(eventId, PageRequest.of(page, size)));
    }

    @PostMapping("/add")
    @Operation(summary = "Add participation to event", description = "Handles the action of admin add new participants")
    public ResponseEntity<EventParticipationResponse> addParticipateAsAdmin(@RequestBody EventParticipationAdmDTO dto) {
        return ResponseEntity.ok(userEventService.addParticipateAsAdminService(dto));
    }

    @GetMapping("/user-comprovant")
    @Operation(summary = "Get payment comprovant of user", description = "Handles to get the comprovant of payment in some event of user ")
    public ResponseEntity<UserPaymentDetailResponse> getUserPaymentDetailResponse(
            @RequestParam(name = "userId") UUID userId, @RequestParam(name = "eventId") UUID eventId) {
        return ResponseEntity.ok(userEventService.getUserPaymentDetail(userId, eventId));
    }

    @GetMapping("/users/not-participate/{event-id}")
    @Operation(summary = "List users that not participate of event")
    public ResponseEntity<Page<User>> listUserThatNotParticipateOfEvent(@PathVariable("event-id") UUID eventId,
                                                                        @RequestParam(name = "page") int page,
                                                                        @RequestParam(name = "size") int size) {
        return ResponseEntity.ok(userEventService.listUsersNotParticipateOfEvent(eventId, PageRequest.of(page, size)));
    }

    @GetMapping("/users/not-participate/name/{event-id}")
    @Operation(summary = "List users that not participate of event by name")
    public ResponseEntity<Page<User>> listUsersThatNotParticipateOfEventByName(@PathVariable("event-id") UUID eventId,
                                                                               @RequestParam(name = "name") String name,
                                                                               @RequestParam(name = "page") int page,
                                                                               @RequestParam(name = "size") int size) {
        return ResponseEntity.ok(
                userEventService.listUsersNotParticipateOfEventByName(eventId, name, PageRequest.of(page, size)));
    }

}
