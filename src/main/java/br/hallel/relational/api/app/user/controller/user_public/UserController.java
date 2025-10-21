package br.hallel.relational.api.app.user.controller.user_public;

import br.hallel.relational.api.app.security.utils.JwtTokenProvider;
import br.hallel.relational.api.app.user.dto.*;
import br.hallel.relational.api.app.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.token.TokenService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "User part for some functions that can do with user authorization")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider tokenService;

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
    public ResponseEntity<UserProfilePreferencesResponse> updateUserPreferences(
            @PathVariable(name = "idUser") UUID idUser, @RequestBody
            UserPreferencesDTO userPreferencesDTO) {
        return ResponseEntity.ok(userService.updateUserPreferences(idUser, userPreferencesDTO));
    }

    @PutMapping("/edit-profile/{idUser}")
    @Operation(
            summary = "Edit Infos User Profile by User ID"
    )
    public ResponseEntity<UserProfileResponseWithToken> editProfile(
            @PathVariable(name = "idUser") UUID idUser,
            @RequestBody UserEditProfileDTO userDto) {
        return ResponseEntity.ok(userService.editProfile(idUser, userDto));
    }

    @PatchMapping(value = "/edit-profile/image/{idUser}", consumes = {"multipart/form-data"})
    @Operation(
            summary = "Edit Image In User Profile by User Id"
    )
    public ResponseEntity<UserProfileResponse> editImageProfile(
            @PathVariable(name = "idUser") UUID idUser,
            @RequestPart(name = "file") MultipartFile fileImage) {
        return ResponseEntity.ok(userService.editImageProfile(idUser, fileImage));
    }

    @GetMapping("/roles/{token}")
    @Operation(summary = "List roles user of user", description = "Route for listing the roles of the user in system")
    public ResponseEntity<List<String>> listRolesUser(@PathVariable("token") String token) {
        return ResponseEntity.ok(this.tokenService.listRolesOfUser(token));

    }

    @PatchMapping("/edit-cpf")
    @Operation(summary = "Edit CPF User ", description = "Edit CPF user info")
    public ResponseEntity<UserEditProfileDTO> editCPFUser(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(name = "cpf") String cpf
    ) {
        return ResponseEntity.ok(this.userService.editCPF(
                tokenService.getUserId(authorizationHeader), cpf));
    }

    @PatchMapping("/edit-phone-number")
    @Operation(summary = "Edit Phone number of user", description = "Handles with updating the phone number of user")
    public ResponseEntity<UserEditProfileDTO> editPhoneNumber(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(name = "phoneNumber") String phoneNumber
    ) {
        return ResponseEntity.ok(
                this.userService.editPhoneNumber(tokenService.getUserId(authorizationHeader), phoneNumber));
    }

    @PatchMapping("/edit-date-birth")
    @Operation(summary = "Edit Date birth of user", description = "Handles with updating the date birth of user")
    public ResponseEntity<UserEditProfileDTO> editDateBirth(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody DateBirthUserDTO dto
    ) {
        return ResponseEntity.ok(
                this.userService.editDateBirth(tokenService.getUserId(authorizationHeader), dto));
    }
}
