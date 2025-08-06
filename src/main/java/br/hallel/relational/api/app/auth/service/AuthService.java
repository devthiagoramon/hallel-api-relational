package br.hallel.relational.api.app.auth.service;

import br.hallel.relational.api.app.auth.dto.LoginRequest;
import br.hallel.relational.api.app.auth.dto.SingUpRequest;
import br.hallel.relational.api.app.auth.dto.TokenAdminResponse;
import br.hallel.relational.api.app.auth.exception.AuthRequestException;
import br.hallel.relational.api.app.security.admin.TokenAdminValidationCode;
import br.hallel.relational.api.app.security.dto.TokenDTO;
import br.hallel.relational.api.app.security.model.Role;
import br.hallel.relational.api.app.security.repository.RoleRepository;
import br.hallel.relational.api.app.security.utils.JwtTokenProvider;
import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.model.UserRole;
import br.hallel.relational.api.app.user.model.UserRoleIds;
import br.hallel.relational.api.app.user.repository.UserRepository;
import br.hallel.relational.api.app.user.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

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

    private final TokenAdminValidationCode tokenAdminValidationCode = new TokenAdminValidationCode();

    @Autowired
    private UserRoleRepository userRoleRepository;

    public TokenDTO login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new AuthRequestException("User not found"));

            var tokenResponse = new TokenDTO();
            tokenResponse = jwtTokenProvider.createAccessToken(loginRequest.getEmail(),
                    user.getRoles().stream().map(Role::getDescription)
                            .toList());
            user.setToken(tokenResponse.getAccessToken());
            userRepository.save(user);
            return tokenResponse;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AuthRequestException("User not found");
        }
    }

    public TokenDTO refreshToken(String email, String refreshToken) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthRequestException("User not found"));
        if (user != null)
            throw new AuthRequestException("User not found");
        var tokenResponse = new TokenDTO();
        tokenResponse = jwtTokenProvider.refreshToken(refreshToken);
        return tokenResponse;
    }

    public TokenDTO singUp(
            SingUpRequest request
    ) {
        log.info("Creating user in system...");
        List<Role> rolesBD = roleRepository.findAll();
        Role userRole = rolesBD.stream()
                .filter(role -> role.getDescription().equals("USER"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Role USER not found"));

        log.info("Add member...");
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDateBirth(null);
        user.setRoles(Set.of(userRole));
        user.setPushNotification(false);
        if (userRepository.
                findByEmail(request.getEmail()).isPresent()) {
            throw new AuthRequestException("User already exists in Database");
        }

        var tokenResponse = new TokenDTO();
        log.info("Antes do token...");
        tokenResponse = jwtTokenProvider.createAccessToken(request.getEmail(), user.getRoles()
                .stream()
                .map(Role::getDescription)
                .toList());
        user.setToken(tokenResponse.getAccessToken());

        User userSaved = userRepository.save(user);

        UserRoleIds userRoleIds = new UserRoleIds(userSaved.getId(), rolesBD.stream()
                .filter(item -> item.getDescription()
                        .equals("USER"))
                .toList()
                .get(0)
                .getId());
        userRoleRepository.save(new UserRole(userRoleIds));
        log.info("SAVING MEMBER...");

        return tokenResponse;
    }

    public TokenAdminResponse verifyIfTokenIsAdmin(String token) {
        boolean isAdmin = jwtTokenProvider.verifyAdminRoleExisting(token);

        if (isAdmin) {
            String code = tokenAdminValidationCode.generateCode();
            User user = this.userRepository.findByToken(token).orElseThrow(() -> new AuthRequestException("User not found with this token"));
            String tokenAdmin = tokenAdminValidationCode.generateToken(user.getId(), code);
            return new TokenAdminResponse(tokenAdmin, code);

        } else {
            log.info("Returning null");
            return null;
        }
    }

    public Boolean validateTokenAdmin(String tokenAdmin, String code) {
        return tokenAdminValidationCode.validateToken(tokenAdmin, code);
    }

    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }
}
