package br.hallel.relational.api.app.auth.controller;

import br.hallel.relational.api.app.auth.dto.GoogleOAuthResponse;
import br.hallel.relational.api.app.auth.dto.LoginRequest;
import br.hallel.relational.api.app.auth.dto.SingUpRequest;
import br.hallel.relational.api.app.auth.dto.TokenAdminResponse;
import br.hallel.relational.api.app.auth.exception.GoogleLoginException;
import br.hallel.relational.api.app.auth.service.AuthService;
import br.hallel.relational.api.app.security.dto.TokenDTO;
import br.hallel.relational.api.app.security.model.Role;
import br.hallel.relational.api.app.security.repository.RoleRepository;
import br.hallel.relational.api.app.security.utils.JwtTokenProvider;
import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication part for acessing the system")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private final AuthService authService;

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final RoleRepository roleRepository;

    @Autowired
    private final JwtTokenProvider jwtService;
    private static final String CLIENT_ID = "1060759694626-c98qb76632sh0ocgm908006ap7gfvur1.apps.googleusercontent.com";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();


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


    @PostMapping("/google/mobile")
    public ResponseEntity<GoogleOAuthResponse> authenticateGoogleMobile(@RequestBody Map<String, String> body) {
        String idTokenRequest = body.get("idTokenRequest");

        System.out.println("Token recebido: " + idTokenRequest);
        try {
            try {
                // Testa se o backend consegue acessar os certificados do Google
                String certs = new NetHttpTransport().createRequestFactory()
                        .buildGetRequest(new GenericUrl("https://www.googleapis.com/oauth2/v3/certs"))
                        .execute()
                        .parseAsString();

                System.out.println("Certificados recebidos do Google:");
                System.out.println(certs);
            } catch (Exception e) {
                System.err.println("❌ Falha ao acessar os certificados públicos do Google:");
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
                TokenDTO jwt = jwtService.createAccessToken(user.getEmail(), user.getRoles()
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

}
