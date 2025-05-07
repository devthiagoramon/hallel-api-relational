package br.hallel.relational.api.app.auth.service;

import br.hallel.relational.api.app.auth.dto.LoginRequest;
import br.hallel.relational.api.app.auth.exception.AuthRequestException;
import br.hallel.relational.api.app.security.dto.TokenDTO;
import br.hallel.relational.api.app.security.model.Role;
import br.hallel.relational.api.app.security.utils.JwtTokenProvider;
import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;


    public TokenDTO login(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new AuthRequestException("User not found"));

        var tokenResponse = new TokenDTO();
        tokenResponse = jwtTokenProvider.createAccessToken(loginRequest.getEmail(), user.getRoles().stream().map(Role::getDescription).toList());
        return tokenResponse;
    }

    public TokenDTO refreshToken(String email, String refreshToken) {
        var user = userRepository.findByEmail(email).orElseThrow(() -> new AuthRequestException("User not found"));
        if (user != null) throw new AuthRequestException("User not found");
        var tokenResponse = new TokenDTO();
        tokenResponse = jwtTokenProvider.refreshToken(refreshToken);
        return tokenResponse;
    }

}
