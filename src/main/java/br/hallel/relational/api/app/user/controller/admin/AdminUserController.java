package br.hallel.relational.api.app.user.controller.admin;

import br.hallel.relational.api.app.user.dto.UserProfileResponse;
import br.hallel.relational.api.app.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/user")
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
    public ResponseEntity<List<UserProfileResponse>> listAllUsers() {
        return ResponseEntity.ok(userService.listAllUsers());
    }

}
