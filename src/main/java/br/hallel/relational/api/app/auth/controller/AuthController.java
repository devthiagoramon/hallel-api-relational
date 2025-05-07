package br.hallel.relational.api.app.auth.controller;

import br.hallel.relational.api.app.auth.dto.LoginRequest;
import br.hallel.relational.api.app.auth.service.AuthService;
import br.hallel.relational.api.app.security.dto.TokenDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication part for acessing the system")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public TokenDTO login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @GetMapping("/refresh-token")
    public TokenDTO refreshToken(@RequestParam(name = "refresh_token") String refreshToken, @RequestParam(name = "email") String email) {
        return authService.refreshToken(refreshToken, email);
    }

}
