package br.hallel.relational.api.app.auth.controller;

import br.hallel.relational.api.app.auth.dto.LoginRequest;
import br.hallel.relational.api.app.auth.dto.SingUpRequest;
import br.hallel.relational.api.app.auth.dto.TokenAdminResponse;
import br.hallel.relational.api.app.auth.service.AuthService;
import br.hallel.relational.api.app.security.dto.TokenDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication part for acessing the system")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private final AuthService authService;

    @PostMapping("/login")
    public TokenDTO login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @GetMapping("/refresh-token")
    public TokenDTO refreshToken(@RequestParam(name = "refresh_token") String refreshToken,
                                 @RequestParam(name = "email") String email) {
        return authService.refreshToken(refreshToken, email);
    }

    @GetMapping("/validate-token")
    public boolean validateToken(@RequestParam(name = "token") String token) {
        return authService.validateToken(token);
    }

    @PostMapping("/sing-up")
    public TokenDTO singUp(@RequestBody SingUpRequest singUpRequest) {
        return authService.singUp(singUpRequest);
    }

    @GetMapping("/verify-admin-access")
    @Operation(summary = "Verify if the user is admin", description = "Verify with the token of user if he is a ADMIN and return the token and the code to validate him")
    public TokenAdminResponse verifyAdminAccess(@RequestParam("token") String token) {
        return authService.verifyIfTokenIsAdmin(token);
    }

    @GetMapping("/validate-admin-access")
    @Operation(summary = "Validate admin token and code", description = "After verify if the user is admin, validate his token and code and return true or false")
    public Boolean validateAdminAccess(@RequestParam("token") String token, @RequestParam("code") String code) {
        return authService.validateTokenAdmin(token, code);
    }
}
