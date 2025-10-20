package br.hallel.relational.api.app.event.controller.user_public;

import br.hallel.relational.api.app.event.dto.EventParticipateDTO;
import br.hallel.relational.api.app.event.dto.EventParticipationResponse;
import br.hallel.relational.api.app.event.service.UserEventService;
import br.hallel.relational.api.app.security.utils.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/public/event/participation")
@RequiredArgsConstructor
@Tag(name = "Public Events - User participation", description = "Public Events User Participation part for public")
public class PublicEventParticipationController {


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
}
