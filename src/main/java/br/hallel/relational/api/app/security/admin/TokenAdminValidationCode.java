package br.hallel.relational.api.app.security.admin;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.NoArgsConstructor;

import java.security.Key;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@NoArgsConstructor
public class TokenAdminValidationCode {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long EXPIRATION = 5 * 60 * 1000; // 5 minutos

    public String generateCode() {
        return String.format("%06d", ThreadLocalRandom.current().nextInt(1000000));
    }

    public String generateToken(UUID idUser, String codigo) {
        return Jwts.builder()
                .setSubject(idUser.toString())
                .claim("codigo", codigo)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key)
                .compact();
    }

    public Boolean validateToken(String token, String codigo) {
        String codigoFromToken = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("codigo", String.class);

        return codigoFromToken.equals(codigo);
    }
}
