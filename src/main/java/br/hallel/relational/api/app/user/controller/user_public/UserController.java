package br.hallel.relational.api.app.user.controller.user_public;

import br.hallel.relational.api.app.user.dto.UserPreferencesDTO;
import br.hallel.relational.api.app.user.dto.UserProfilePreferencesResponse;
import br.hallel.relational.api.app.user.dto.UserProfileResponse;
import br.hallel.relational.api.app.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "User part for some functions that can do with user authorization")
public class UserController {

    private final UserService userService;

    @GetMapping("/preferences/{idUser}")
    @Operation(
            summary = "Get user preferences ",
            description = "Route to list preferences of user like notification"
    )
    public ResponseEntity<UserProfilePreferencesResponse> getUserPreferences(
            @PathVariable(name = "idUser") UUID idUser) {
        return ResponseEntity.ok(userService.getUserPreferences(idUser));
    }

    @PatchMapping("/preferences/update/{idUser}")
    @Operation(
            summary = "Update user preferences",
            description = "Route to update user preferences like notification"
    )
    public ResponseEntity<UserProfilePreferencesResponse> updateUserPreferences(@PathVariable(name = "idUser") UUID idUser, @RequestBody
                                                                                UserPreferencesDTO userPreferencesDTO) {
        return ResponseEntity.ok(userService.updateUserPreferences(idUser, userPreferencesDTO));
    }
}
