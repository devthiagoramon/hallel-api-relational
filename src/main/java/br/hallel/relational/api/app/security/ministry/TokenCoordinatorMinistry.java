package br.hallel.relational.api.app.security.ministry;

import br.hallel.relational.api.app.ministry.model.MemberMinistry;
import br.hallel.relational.api.app.ministry.model.RoleMinistry;
import br.hallel.relational.api.app.ministry.repository.MemberMinistryRepository;
import br.hallel.relational.api.app.security.dto.TokenCoordinatorDTO;
import br.hallel.relational.api.app.security.dto.TokenDTO;
import br.hallel.relational.api.app.security.exceptions.InvalidJwtAuthenticationException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class TokenCoordinatorMinistry {


    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    private long expirationTimeInMiliseconds = 2592000000L;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private MemberMinistryRepository memberMinistryRepository;

    Algorithm algorithm = null;

    @PostConstruct
    public void init() {
        this.algorithm = Algorithm.HMAC256(this.secretKey.getEncoded());
    }

    public TokenCoordinatorDTO createAccessToken(UUID userId, UUID ministryId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationTimeInMiliseconds);
        MemberMinistry memberMinistry = memberMinistryRepository.findMemberMinistryByUser_IdAndMinistry_Id(userId,
                ministryId).orElseThrow(() -> new InvalidJwtAuthenticationException("Member ministry not found"));
        var accessToken = getAccessToken(userId, ministryId, now, expiration, memberMinistry);
        var refreshToken = getRefreshToken(userId, ministryId, now);
        return new TokenCoordinatorDTO(userId, ministryId, expiration, accessToken, refreshToken);
    }

    public TokenCoordinatorDTO refreshToken(String refreshToken) {
        if (refreshToken.contains("Bearer ")) refreshToken = refreshToken.substring("Bearer ".length());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(refreshToken);
        UUID userId = UUID.fromString(decodedJWT.getSubject());
        UUID ministryId = UUID.fromString(decodedJWT.getClaim("ministryId").toString());
        return createAccessToken(userId, ministryId);
    }


    private String getAccessToken(UUID userId, UUID ministryId,
                                  Date now, Date expiration, MemberMinistry memberMinistry) {
        String issuerUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .build()
                .toUriString();
        return JWT.create()
                .withClaim("memberMinistryId", userId.toString())
                .withClaim("ministryId", ministryId.toString())
                .withClaim("roles",
                        memberMinistry.getMinistryRoles().stream().map(RoleMinistry::getDescription).toList())
                .withIssuedAt(now)
                .withExpiresAt(expiration)
                .withIssuer(issuerUrl)
                .withSubject(userId.toString())
                .sign(algorithm).strip();

    }

    private String getRefreshToken(UUID userId, UUID ministryId, Date now) {
        Date expirationRefreshToken = new Date(now.getTime() + expirationTimeInMiliseconds * 3);

        return JWT.create()
                .withClaim("memberMinistryId", userId.toString())
                .withClaim("ministryId", ministryId.toString())
                .withIssuedAt(now)
                .withExpiresAt(expirationRefreshToken)
                .sign(algorithm).strip();
    }

    public Authentication getAuthentication(String token) {
        DecodedJWT decodedJWT = decodedToken(token);
        String userDetails = decodedJWT.getSubject();

        List<String> roles = decodedJWT.getClaim("roles").asList(String.class);
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities); // <-- Usa as authorities do token
    }

    private DecodedJWT decodedToken(String token) {
        Algorithm alg = Algorithm.HMAC256(secretKey.getEncoded());
        JWTVerifier verifier = JWT.require(alg).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT;
    }

    public String resolveToken(HttpServletRequest request) {
        String token = request.getHeader("coordenador-token");
        if (token != null && !token.isBlank()) {
            return token;
        }
        return null;
    }

    public boolean validateToken(String token) {
        DecodedJWT decodedJWT = decodedToken(token);
        try {
            return !decodedJWT.getExpiresAt().before(new Date());
        } catch (Exception e) {
            throw new InvalidJwtAuthenticationException("Expired or invalid JWT token!");
        }

    }

}
