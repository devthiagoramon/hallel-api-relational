package br.hallel.relational.api.app.security.ministry;

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

    Algorithm algorithm = null;

    @PostConstruct
    public void init() {
        this.algorithm = Algorithm.HMAC256(this.secretKey.getEncoded());
    }

    public TokenCoordinatorDTO createAccessToken(UUID userId, UUID ministryId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationTimeInMiliseconds);
        var accessToken = getAccessToken(userId,ministryId, now, expiration);
        var refreshToken = getRefreshToken(userId, ministryId, now);
        return new TokenCoordinatorDTO(userId, ministryId, expiration, accessToken, refreshToken);
    }

    public TokenCoordinatorDTO refreshToken(String refreshToken){
        if (refreshToken.contains("Bearer ")) refreshToken = refreshToken.substring("Bearer ".length());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(refreshToken);
        UUID userId = UUID.fromString(decodedJWT.getSubject());
        UUID ministryId = UUID.fromString(decodedJWT.getClaim("ministryId").toString());
        return createAccessToken(userId,  ministryId);
    }


    private String getAccessToken(UUID userId, UUID ministryId,
                                  Date now, Date expiration) {
        String issuerUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                                                      .build()
                                                      .toUriString();
        return JWT.create()
                  .withClaim("userId", userId.toString())
                  .withClaim("ministryId", ministryId.toString())
                  .withIssuedAt(now)
                  .withExpiresAt(expiration)
                  .withIssuer(issuerUrl)
                    .withSubject(userId.toString())
                  .sign(algorithm).strip();

    }

    private String getRefreshToken(UUID userId, UUID ministryId, Date now) {
        Date expirationRefreshToken = new Date(now.getTime() + expirationTimeInMiliseconds * 3);

        return JWT.create()
                  .withClaim("userId", userId.toString())
                  .withClaim("ministryId", ministryId.toString())
                  .withIssuedAt(now)
                  .withExpiresAt(expirationRefreshToken)
                  .sign(algorithm).strip();
    }

    public Authentication getAuthentication(String token) {
        DecodedJWT decodedJWT = decodedToken(token);
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(decodedJWT.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private DecodedJWT decodedToken(String token) {
        Algorithm alg = Algorithm.HMAC256(secretKey.getEncoded());
        JWTVerifier verifier = JWT.require(alg).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT;
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring("Bearer ".length());
        }
        return null;
    }

    public boolean validateToken(String token){
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

}
