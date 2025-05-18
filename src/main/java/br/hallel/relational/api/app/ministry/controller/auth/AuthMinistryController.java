package br.hallel.relational.api.app.ministry.controller.auth;

import br.hallel.relational.api.app.ministry.exception.CoordinatorNotFoundException;
import br.hallel.relational.api.app.ministry.service.MinistryService;
import br.hallel.relational.api.app.security.dto.TokenCoordinatorDTO;
import br.hallel.relational.api.app.security.dto.TokenDTO;
import br.hallel.relational.api.app.security.ministry.TokenCoordinatorMinistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/auth/ministry")
@Tag(name = "Ministry Authentication",
     description = "Authentication part for acessing the ministry module as coordinator")
@RequiredArgsConstructor
public class AuthMinistryController {

    private final MinistryService ministryService;
    private final TokenCoordinatorMinistry tokenCoordinatorMinistry;

    @GetMapping("/generate-token")
    @Operation(summary = "Generate token for coordinator",
               description = "This route generate token to access the infos and routes of ministry as coordinator, but only get the token if the user is coordinator of ministry passed in paramenters")
    public ResponseEntity<TokenCoordinatorDTO> generateTokenCoordinator(
            @RequestParam(name = "ministryId")
            UUID ministryId,
            @RequestParam(name = "userId") UUID userId) {
        if (!(ministryService.validateCoordinatorOfMinistry(ministryId, userId))){
            throw new CoordinatorNotFoundException("Can't find coordinator of ministry %s by id %s".formatted(ministryId, userId));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(tokenCoordinatorMinistry.createAccessToken(userId, ministryId));
    }
}
