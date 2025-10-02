package br.hallel.relational.api.app.security.utils;

import br.hallel.relational.api.app.security.dto.TokenDTO;
import br.hallel.relational.api.app.security.exceptions.InvalidJwtAuthenticationException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class JwtTokenProvider {

    private String secretKey = "secret";
    private long expirationTimeInMiliseconds = 2592000000L;

    @Autowired
    private UserDetailsService userDetailsService;

    Algorithm algorithm = null;

    @PostConstruct
    protected void init() {
        algorithm = Algorithm.HMAC256(secretKey.getBytes());

    }

    public TokenDTO createAccessToken(UUID userId, String username, List<String> roles) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationTimeInMiliseconds);
        var accessToken = getAccessToken(userId, username, roles, now, expiration);
        var refreshToken = getRefreshToken(userId, username, roles, now);
        return new TokenDTO(username, true, now, expiration, accessToken, refreshToken);
    }

    public TokenDTO refreshToken(String refreshToken) {
        if (refreshToken.contains("Bearer ")) refreshToken = refreshToken.substring("Bearer ".length());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(refreshToken);
        String username = decodedJWT.getSubject();
        List<String> roles = decodedJWT.getClaim("roles").asList(String.class);
        UUID userId = UUID.fromString(decodedJWT.getClaim("userId").asString());
        return createAccessToken(userId, username, roles);
    }


    private String getAccessToken(UUID userId, String username, List<String> roles,
                                  Date now, Date expiration) {
        String issuerUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .build()
                .toUriString();
        return JWT.create()
                .withClaim("roles", roles)
                .withIssuedAt(now)
                .withExpiresAt(expiration)
                .withSubject(username)
                .withIssuer(issuerUrl)
                .withClaim("userId", userId.toString())
                .sign(algorithm);

    }

    private String getRefreshToken(UUID userId, String username,
                                   List<String> roles, Date now) {
        Date expirationRefreshToken = new Date(now.getTime() + expirationTimeInMiliseconds * 3);

        return JWT.create()
                .withClaim("roles", roles)
                .withIssuedAt(now)
                .withExpiresAt(expirationRefreshToken)
                .withSubject(username)
                .withClaim("userId", userId.toString())
                .sign(algorithm);
    }

    public Authentication getAuthentication(String token) {
        DecodedJWT decodedJWT = decodedToken(token);
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(decodedJWT.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private DecodedJWT decodedToken(String token) {
        Algorithm alg = Algorithm.HMAC256(secretKey.getBytes());
        JWTVerifier verifier = JWT.require(alg).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT;
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring("Bearer ".length()).trim();
        }
        return null;
    }

    public String resolveTokenString(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring("Bearer ".length()).trim();
        }
        return null;
    }

    public UUID getUserId(String token) {
        DecodedJWT decodedJWT = decodedToken(resolveTokenString(token));
        return UUID.fromString(decodedJWT.getClaim("userId").asString());
    }

    public String getSubject(String token) {
        DecodedJWT decodedJWT = decodedToken(token);
        return decodedJWT.getSubject();
    }

    public boolean validateToken(String token) {
        DecodedJWT decodedJWT = decodedToken(token);
        try {
            if (decodedJWT.getExpiresAt().before(new Date())) {
                return false;
            }
            return true;
        } catch (Exception e) {
            throw new InvalidJwtAuthenticationException("Expired or invalid JWT token!");
        }

    }

    public boolean verifyAdminRoleExisting(String token) {
        DecodedJWT decodedJWT = decodedToken(token);
        List<String> roles = decodedJWT.getClaim("roles").asList(String.class);
        return roles.contains("ADMIN");
    }


    public boolean validateTokenOfAdmin(String token) {
        DecodedJWT decodedJWT = decodedToken(token);
        try {
            if (decodedJWT.getExpiresAt().before(new Date())) {
                return false;
            }
            return decodedJWT.getClaim("roles").asList(String.class).contains("ADMIN");
        } catch (Exception e) {
            throw new InvalidJwtAuthenticationException("Expired or invalid JWT token!");
        }
    }

    public List<String> listRolesOfUser(String token) {
        DecodedJWT decodedJWT = decodedToken(token);
        return decodedJWT.getClaim("roles").asList(String.class);
    }
}