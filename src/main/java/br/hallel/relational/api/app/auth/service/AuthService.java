package br.hallel.relational.api.app.auth.service;

import br.hallel.relational.api.app.auth.dto.LoginRequest;
import br.hallel.relational.api.app.auth.dto.SingUpRequest;
import br.hallel.relational.api.app.auth.exception.AuthRequestException;
import br.hallel.relational.api.app.security.dto.TokenDTO;
import br.hallel.relational.api.app.security.model.Role;
import br.hallel.relational.api.app.security.repository.RoleRepository;
import br.hallel.relational.api.app.security.utils.JwtTokenProvider;
import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.model.UserStatus;
import br.hallel.relational.api.app.user.repository.UserRepository;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;

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

    public TokenDTO singUp(
            SingUpRequest request
    ) {
        HashSet<Role> roles = new HashSet<>();
        List<Role> rolesBD = roleRepository.findAll();
        rolesBD.forEach(role -> {
            System.out.println(role.getDescription());
            if (role.getDescription().equals("USER")) {
                roles.add(role);
            }
        });
        log.info("Roles found: " + roles);
        log.info("Add member...");
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(roles);
        user.setDateBirth(new Date());
        if (userRepository.
                findByEmail(request.getEmail()).isPresent()) {
            throw new AuthRequestException("User already exists in Database");
        }
        log.info(user.getRoles().toString());
        var tokenResponse = new TokenDTO();
        log.info("Antes do token...");
        tokenResponse = jwtTokenProvider.createAccessToken(request.getEmail(), user.getRoles().stream().map(Role::getDescription).toList());

        user.setToken(tokenResponse.getAccessToken());

        userRepository.save(user);
        log.info("SAVING MEMBER...");

        return tokenResponse;
    }
}
