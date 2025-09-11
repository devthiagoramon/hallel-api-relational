package br.hallel.relational.api.app.user.controller.admin;

import br.hallel.relational.api.app.user.dto.CreateEditUser;
import br.hallel.relational.api.app.user.dto.UserProfileResponse;
import br.hallel.relational.api.app.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @PathVariable(name = "idUser") UUID idUser,
            @RequestParam(name = "idEventScale", required = false) UUID idEventScale) {
        return ResponseEntity.ok(userService.getUserProfile(idUser, idEventScale));
    }

    @GetMapping("/list-all")
    public ResponseEntity<Page<UserProfileResponse>> listAllUsers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.listAllUsers(page, size));
    }

    @GetMapping("/list-all/by-name/{name}")
    public ResponseEntity<Page<UserProfileResponse>> listAllUsersByName(
            @PathVariable(name = "name") String name,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.listAllUsersByName(name, page, size));
    }

    @PostMapping
    @Operation(summary = "Create user in system", description = "Handles creating of user in system")
    public ResponseEntity<UserProfileResponse> createUser(@RequestBody CreateEditUser dto) {
        return ResponseEntity.ok(userService.createUser(dto));
    }

    @PutMapping("/{idUser}")
    @Operation(summary = "Edit user in system", description = "Handles edit user in system")
    public ResponseEntity<UserProfileResponse> editUser(@PathVariable("idUser") UUID id,
                                                        @RequestBody CreateEditUser dto) {
        return ResponseEntity.ok(userService.editUser(id, dto));
    }

    @PatchMapping("/disable/{idUser}")
    @Operation(summary = "Disable user in system", description = "Handles disable user account")
    public ResponseEntity<UserProfileResponse> disableUser(@PathVariable("idUser") UUID idUser) {
        return ResponseEntity.ok(userService.disableUser(idUser));
    }

    @PatchMapping("/activate/{idUser}")
    @Operation(summary = "Activate user in system", description = "Handles activate user account")
    public ResponseEntity<UserProfileResponse> activateUser(@PathVariable("idUser") UUID idUser) {
        return ResponseEntity.ok(userService.activateUser(idUser));
    }
}
