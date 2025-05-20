package br.hallel.relational.api.app.user.controller;

import br.hallel.relational.api.app.user.dto.UserEditProfileDTO;
import br.hallel.relational.api.app.user.dto.UserEditProfileResponse;
import br.hallel.relational.api.app.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/public/user/")
@RequiredArgsConstructor
@Tag(name = "User", description = "User part for some functions that can do ")
public class UserController {

    @Autowired
    private UserService userService;

    @PutMapping("/edit-profile/{idUser}")
    public ResponseEntity<UserEditProfileResponse> editProfile(
            @PathVariable(name = "idUser") UUID idUser,
            @RequestBody UserEditProfileDTO userDto) {
        System.out.println("DATA:" + userDto.dateBirth());
        return ResponseEntity.ok(userService.editProfile(idUser, userDto));
    }

    @PatchMapping("/edit-profile/image/{idUser}")
    public ResponseEntity<UserEditProfileResponse> editImageProfile(
            @PathVariable(name = "idUser") UUID idUser,
            @RequestPart(name = "file") MultipartFile fileImage) {
        return ResponseEntity.ok(userService.editImageProfile(idUser, fileImage));
    }
}
