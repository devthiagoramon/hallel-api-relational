package br.hallel.relational.api.app.user.controller.user_public;

import br.hallel.relational.api.app.event.dto.GuestInvitedEventScaleResponse;
import br.hallel.relational.api.app.event.service.MemberEventScaleService;
import br.hallel.relational.api.app.user.dto.UserEditProfileDTO;
import br.hallel.relational.api.app.user.dto.UserProfileResponse;
import br.hallel.relational.api.app.user.dto.UserProfileResponseWithToken;
import br.hallel.relational.api.app.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/public/user")
@RequiredArgsConstructor
@Tag(name = "User - Public", description = "User part for some functions that can do in public way ")
public class UserPublicController {

    private final UserService userService;
    private final MemberEventScaleService memberEventScaleService;


    @GetMapping("/profile/{idUser}")
    @Operation(
            summary = "Get User Infos to Profile"
    )
    public ResponseEntity<UserProfileResponse> getUserProfile(
            @PathVariable(name = "idUser") UUID idUser,
            @RequestParam(name = "idEventScale", required = false) UUID idEventScale) {
        return ResponseEntity.ok(userService.getUserProfile(idUser, idEventScale));
    }


    @GetMapping("/profile/token/{accessToken}")
    @Operation(summary = "Get profile infos by token", description = "Get the infos of user with token related with user")
    public ResponseEntity<UserProfileResponse> getUserProfileByToken(@PathVariable(name = "accessToken") String token) {
        return ResponseEntity.ok(userService.getUserProfileByToken(token));
    }

    @GetMapping("/list-all/guests-scale/{id}")
    @Operation(
            summary = "Listing all Guests in Event Scale"
    )
    public ResponseEntity<List<GuestInvitedEventScaleResponse>> listAllGuestsInScaleByID_UserInfo(
            @PathVariable(name = "id") UUID id) {
        return ResponseEntity.ok(this.memberEventScaleService.listAllGuestsInvitedsByEventScaleId(id));
    }


}
