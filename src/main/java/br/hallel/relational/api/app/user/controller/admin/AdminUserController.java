package br.hallel.relational.api.app.user.controller.admin;

import br.hallel.relational.api.app.user.dto.UserProfileResponse;
import br.hallel.relational.api.app.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/public/user")
@RequiredArgsConstructor
@Tag(name = "User - Admin", description = "Admin part for users managment")
public class AdminUserController {
    @Autowired
    private UserService userService;

    @GetMapping("/get/{idUser}")
    public ResponseEntity<UserProfileResponse> getUserById(
            @PathVariable(name = "idUser") UUID idUser) {
        return ResponseEntity.ok(userService.getUserProfile(idUser));
    }

    @GetMapping("/list-all")
    public ResponseEntity<List<UserProfileResponse>> listAllUsers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.listAllUsers(page, size ));
    }

}
