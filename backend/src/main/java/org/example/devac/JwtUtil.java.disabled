package org.example.devac.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;
    private final long expirationMs;

    public JwtUtil(@Value("${jwt.secret:}") String secretFromProps,
                   @Value("${jwt.expirationMs:3600000}") long expirationMs) {
        // primero busca en application properties, si no encuentra, busca en env, y si tampoco esta, lanza excepcion
        String secret = (secretFromProps == null || secretFromProps.isBlank())
                ? System.getenv("JWT_SECRET")
                : secretFromProps;

        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret no encontrado. Define 'jwt.secret' en application.properties o la variable de entorno JWT_SECRET");
        }

        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT secret demasiado corta. Debe tener al menos 32 bytes de longitud");
        }

        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
    }

    public String generateToken(String username) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationMs))
                .signWith(key)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
