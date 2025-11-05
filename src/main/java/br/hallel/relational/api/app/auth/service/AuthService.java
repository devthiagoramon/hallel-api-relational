package br.hallel.relational.api.app.auth.service;

import br.hallel.relational.api.app.auth.dto.LoginRequest;
import br.hallel.relational.api.app.auth.dto.SignUpRequest;
import br.hallel.relational.api.app.auth.dto.TokenAdminResponse;
import br.hallel.relational.api.app.auth.exception.AuthRequestException;
import br.hallel.relational.api.app.email.service.EmailAuthService;
import br.hallel.relational.api.app.event.utils.EventParticipationUtils;
import br.hallel.relational.api.app.security.admin.TokenAdminValidationCode;
import br.hallel.relational.api.app.security.dto.TokenDTO;
import br.hallel.relational.api.app.security.model.Role;
import br.hallel.relational.api.app.security.repository.RoleRepository;
import br.hallel.relational.api.app.security.utils.JwtTokenProvider;
import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.model.UserAccountStatus;
import br.hallel.relational.api.app.user.model.UserRole;
import br.hallel.relational.api.app.user.model.UserRoleIds;
import br.hallel.relational.api.app.user.repository.UserRepository;
import br.hallel.relational.api.app.user.repository.UserRoleRepository;
import br.hallel.relational.api.app.user.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    private final TokenAdminValidationCode tokenAdminValidationCode = new TokenAdminValidationCode();
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final EmailAuthService emailAuthService;

    private final UserRoleRepository userRoleRepository;
    private final EventParticipationUtils utils;

    public TokenDTO login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new AuthRequestException("User not found"));

            var tokenResponse = new TokenDTO();
            tokenResponse = jwtTokenProvider.createAccessToken(user.getId(), loginRequest.getEmail(),
                    user.getRoles().stream().map(Role::getDescription)
                            .toList());
            user.setToken(tokenResponse.getAccessToken());
            if (user.getStatus() == UserAccountStatus.DISABLED) {
                user.setStatus(UserAccountStatus.ENABLED);
            }
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

    public TokenDTO signUp(SignUpRequest request) {
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
        user.setStatus(UserAccountStatus.ENABLED);

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AuthRequestException("User already exists in Database");
        }

        User userSaved = userRepository.save(user);

        log.info("Gerando token...");
        var tokenResponse = jwtTokenProvider.createAccessToken(
                userSaved.getId(),
                userSaved.getEmail(),
                userSaved.getRoles().stream().map(Role::getDescription).toList()
        );

        userSaved.setToken(tokenResponse.getAccessToken());
        userRepository.save(userSaved);

        UserRoleIds userRoleIds = new UserRoleIds(
                userSaved.getId(),
                userRole.getId()
        );
        userRoleRepository.save(new UserRole(userRoleIds));
        String[] nameParts = UserUtils.splitFullName(user.getName());
        String nameFormatted = nameParts[0] + " " + nameParts[1];
        emailAuthService.sendSignUpMail(user.getEmail(), nameFormatted
        );
        log.info("SAVING MEMBER...");

        return tokenResponse;
    }

    public TokenAdminResponse verifyIfTokenIsAdmin(String token) {
        boolean isAdmin = jwtTokenProvider.verifyAdminRoleExisting(token);

        if (isAdmin) {
            String code = tokenAdminValidationCode.generateCode();
            User user = this.userRepository.findByToken(token)
                    .orElseThrow(() -> new AuthRequestException("User not found with this token"));
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

    public TokenAdminResponse verifyIfTokenIsAdminWeb(String token) {
        log.info("Verifying if admin token is valid and verifying the admin...");
        boolean isAdmin = jwtTokenProvider.verifyAdminRoleExisting(token);

        if (isAdmin) {
            String code = tokenAdminValidationCode.generateCode();
            User user = this.userRepository.findByToken(token)
                    .orElseThrow(() -> new AuthRequestException("User not found with this token"));
            String tokenAdmin = tokenAdminValidationCode.generateToken(user.getId(), code);
            String ngrokUrl = getNgrokUrl();
            String url = String.format(
                    "https://api.comunidadecatolicahallel.com.br/auth/validate-admin-access-web/%s?token=%s", code,
                    tokenAdmin);

            if (ngrokUrl == null) {
                log.info("https://api.comunidadecatolicahallel.com.br/auth/validate-admin-access-web/{}?token={}", code,
                        tokenAdmin);
            } else {
                log.info("{}/auth/validate-admin-access-web/{}?token={}", ngrokUrl, code, tokenAdmin);
            }

            if (ngrokUrl == null) {
                emailAuthService.sendAdminMail(user.getEmail(), user.getEmail(), url);
            }
            return new TokenAdminResponse(tokenAdmin, code);
        }
        return null;
    }

    private String getNgrokUrl() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());

            String response = restTemplate.getForObject("http://localhost:4040/api/tunnels", String.class);

            if (response != null) {
                // Parse simples para pegar a URL HTTPS
                String[] parts = response.split("\"public_url\":\"https://");
                if (parts.length > 1) {
                    String url = "https://" + parts[1].split("\"")[0];
                    return url;
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️  Ngrok não detectado na porta 4040");
        }
        return null;
    }


    public Boolean validateTokenAdminWeb(String tokenAdmin, String code) {
        log.info("Validating if admin token and code is valid...");
        Boolean isValid = tokenAdminValidationCode.validateToken(tokenAdmin, code);
        String destination = "/queue/auth/admin/" + code;
        if (isValid) {
            simpMessagingTemplate.convertAndSend(destination, true);
        } else {
            simpMessagingTemplate.convertAndSend(destination, false);
        }
        return isValid;
    }

    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    public boolean validateTokenOfAdmin(String token) {
        return jwtTokenProvider.validateTokenOfAdmin(token);
    }
}
