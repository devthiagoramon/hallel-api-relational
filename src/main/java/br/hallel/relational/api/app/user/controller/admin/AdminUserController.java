package br.hallel.relational.api.app.user.controller.admin;

import br.hallel.relational.api.app.user.dto.*;
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
            @PathVariable(name = "idUser") UUID idUser) {
        return ResponseEntity.ok(userService.getUserProfile(idUser, null));
    }

    @GetMapping("/list-all")
    public ResponseEntity<Page<UserProfileResponseWithRole>> listAllUsers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "filter", required = false) String filterAuthorities
    ) {

        String nameFiltered = null;
        FilterAuthorietiesDTO filterAuthorietiesDTO = null;

        if (name != null && !name.equalsIgnoreCase("undefined")) {
            nameFiltered = name;
        }

        if (filterAuthorities != null && !filterAuthorities.equalsIgnoreCase("undefined")) {
            filterAuthorietiesDTO = FilterAuthorietiesDTO.valueOf(filterAuthorities);
        }
        return ResponseEntity.ok(userService.listAllUsers(page, size, nameFiltered, filterAuthorietiesDTO));
    }

    @GetMapping("/list-all/by-name/{name}")
    public ResponseEntity<Page<UserProfileResponseWithRole>> listAllUsersByName(
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

    @PatchMapping("/update/role")
    @Operation(summary = "Update role of user in system", description = "Handles when admin want to update the role of user in system")
    public ResponseEntity<UserProfileResponse> updateRoleUser(@RequestBody UpdateRoleUserDTO dto) {
        return ResponseEntity.ok(this.userService.updateRoleOfUser(dto));
    }
}
