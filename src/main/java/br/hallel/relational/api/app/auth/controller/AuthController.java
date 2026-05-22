package br.hallel.relational.api.app.auth.controller;

import br.hallel.relational.api.app.auth.dto.GoogleOAuthResponse;
import br.hallel.relational.api.app.auth.dto.LoginRequest;
import br.hallel.relational.api.app.auth.dto.SignUpRequest;
import br.hallel.relational.api.app.auth.dto.TokenAdminResponse;
import br.hallel.relational.api.app.auth.exception.GoogleLoginException;
import br.hallel.relational.api.app.auth.service.AuthService;
import br.hallel.relational.api.app.security.dto.TokenDTO;
import br.hallel.relational.api.app.security.model.Role;
import br.hallel.relational.api.app.security.repository.RoleRepository;
import br.hallel.relational.api.app.security.utils.JwtTokenProvider;
import br.hallel.relational.api.app.user.exceptions.UserNotFoundException;
import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.repository.UserRepository;
import br.hallel.relational.api.app.user.service.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication part for acessing the system")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;

    private final JwtTokenProvider jwtService;
    private static final String CLIENT_ID = "1060759694626-c98qb76632sh0ocgm908006ap7gfvur1.apps.googleusercontent.com";
    private final JwtTokenProvider jwtTokenProvider;


    @PostMapping("/login")
    public TokenDTO login(@RequestBody LoginRequest loginRequest) {
        System.out.println("LOGIN REALIZADO!!!");
        this.userService.registerLastActivity(loginRequest.getEmail(), null);
        return authService.login(loginRequest);
    }

    @GetMapping("/refresh-token")
    public TokenDTO refreshToken(@RequestParam(name = "refresh_token") String refreshToken,
                                 @RequestParam(name = "email") String email) {
        System.out.println("REFRESH REALIZADO!!!");
        this.userService.registerLastActivity(email, null);
        return authService.refreshToken(refreshToken, email);
    }

    @GetMapping("/validate-token")
    @Operation(summary = "Validate user token", description = "Verify the expiration of token of user")
    public boolean validateToken(@RequestParam(name = "token") String token) {
        userService.registerLastActivity(null, token);
        return authService.validateToken(token);
    }

    @GetMapping("/validate-token-admin")
    @Operation(summary = "Validate admin token", description = "Verify authorities and expiration token of admin")
    public boolean validateTokenAdmin(@RequestParam(name = "token") String token) {

        return authService.validateTokenOfAdmin(token);
    }

    @PostMapping("/sign-up")
    public TokenDTO signUp(@RequestBody SignUpRequest signUpRequest) {
        return authService.signUp(signUpRequest);
    }

    @GetMapping("/verify-admin-access")
    @Operation(summary = "Verify if the user is admin", description = "Verify with the token of user if he is a ADMIN and return the token and the code to validate him")
    public TokenAdminResponse verifyAdminAccess(@RequestParam("token") String token) {
        TokenAdminResponse tokenAdminResponse = authService.verifyIfTokenIsAdmin(token);
        System.out.println(tokenAdminResponse.getCode());
        return tokenAdminResponse;
    }

    @GetMapping("/validate-admin-access")
    @Operation(summary = "Validate admin token and code", description = "After verify if the user is admin, validate his token and code and return true or false")
    public Boolean validateAdminAccess(@RequestParam("token") String token, @RequestParam("code") String code) {
        return authService.validateTokenAdmin(token, code);
    }

    @GetMapping("/validate-admin-access-web/{validationCode}")
    @Operation(summary = "Validate code of administration in web",
            description = "This routes handles the web administrator login, verify via socket the administrator authorities")
    public ResponseEntity<String> validateAdminAccessWeb(@RequestParam(name = "token") String token,
                                                         @PathVariable("validationCode") String code,
                                                         @RequestParam(name = "accessToken") String accessToken
    ) {
        boolean isValid = authService.validateTokenAdminWeb(token, code);

        String baseUrl;

        if (authService.getNgrokUrl() == null) {
            baseUrl = String.format("https://comunidadecatolicahallel.com.br/administrador");
        } else {
            baseUrl = String.format("http://localhost:5173/administrador");
        }

        if (isValid) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(String.format("%s/auth-callback?token=%s&accessToken=%s", baseUrl,
                            token, accessToken)))
                    .build();
        } else {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(String.format("%s/auth-callback?error=invalid_token", baseUrl)))
                    .build();
        }
    }

    @GetMapping("/verify-admin-access-web/{token}")
    @Operation(summary = "Verify code of administrator in web",
            description = "This routes handles the web administrator login attempt, sending to adm email with token and url")
    public TokenAdminResponse verifyAdminAccessWeb(@PathVariable("token") String token) {
        return authService.verifyIfTokenIsAdminWeb(token);
    }

    @PostMapping("/google/mobile")
    public ResponseEntity<GoogleOAuthResponse> authenticateGoogleMobile(@RequestBody Map<String, String> body) {
        String idTokenRequest = body.get("idTokenRequest");

        System.out.println("Token recebido: " + idTokenRequest);
        try {
            try {
                String certs = new NetHttpTransport().createRequestFactory()
                        .buildGetRequest(new GenericUrl("https://www.googleapis.com/oauth2/v3/certs"))
                        .execute()
                        .parseAsString();

                System.out.println("Certificados recebidos do Google:");
                System.out.println(certs);
            } catch (Exception e) {
                System.err.println("Falha ao acessar os certificados públicos do Google:");
                e.printStackTrace();
            }


            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier
                    .Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                    .setAudience(Collections.singleton(CLIENT_ID))
                    .build();
            GoogleIdToken idToken = verifier.verify(idTokenRequest);
            if (idToken == null) {
                System.out.println("Token inválido: falha na verificação");
            }

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                String name = (String) payload.get("name");

                System.out.println("Email: " + payload.getEmail());
                System.out.println("Name: " + payload.get("name"));

                Optional<User> userOptional = this.userRepository.findByEmail(email);
                User user = userOptional.orElseGet(() -> {
                    User user_member = new User();
                    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                    String senhaAleatoria = UUID.randomUUID()
                            .toString()
                            .substring(0, 8);
                    HashSet<Role> roles = new HashSet<>();
                    List<Role> rolesBD = this.roleRepository.findAll();

                    rolesBD.forEach(role -> {
                        if (role.getDescription().equalsIgnoreCase("USER")) {
                            roles.add(role);
                        }
                    });
                    user_member.setEmail(email);
                    user_member.setName(name);
                    user_member.setPassword(encoder.encode(senhaAleatoria));
                    user_member.setRoles(roles);
                    user_member.setPushNotification(false);
                    return this.userRepository.save(user_member);
                });
                TokenDTO jwt = jwtService.createAccessToken(user.getId(), user.getEmail(), user.getRoles()
                        .stream()
                        .map(Role::getDescription)
                        .toList());
                return ResponseEntity.ok(new GoogleOAuthResponse(jwt.getAccessToken(), user));
            }
        } catch (Exception e) {
            throw new GoogleLoginException("Erro na validação de Token");
        }
        throw new GoogleLoginException("Erro na validação de Token");
    }

    @GetMapping("/auto-login/{userToken}")
    @Operation(summary = "Auto login with user token from email")
    public ResponseEntity<TokenDTO> autoLogin(@PathVariable String userToken) {
        try {
            log.info("🔍 Tentando auto-login com token: {}", userToken);

            User user = userRepository.findByToken(userToken)
                    .orElseThrow(() -> new UserNotFoundException("user.not.found", userToken));

            log.info("✅ Usuário encontrado: {} - Email: {}", user.getId(), user.getEmail());

            TokenDTO tokenDTO = new TokenDTO(
                    user.getUsername(),
                    true,
                    new Date(),
                    new Date(System.currentTimeMillis() + 2592000000L),
                    user.getToken(),
                    "refresh-token-placeholder"
            );

            log.info("🎉 Auto-login bem-sucedido para usuário: {}", user.getEmail());

            return ResponseEntity.ok(tokenDTO);

        } catch (UserNotFoundException e) {
            log.error("🚫 Token de usuário inválido: {}", userToken);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            log.error("💥 Erro no auto-login: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
