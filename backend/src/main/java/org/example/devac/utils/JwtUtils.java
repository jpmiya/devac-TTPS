package org.example.devac.utils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

public class JwtUtils {
    
    private static final String secret = System.getenv("TOKEN_JWT"); // lee el token desde env, cargado por docker.

    private static final Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));


    public static String generateToken(Long userId) {
        

        //return token read from application.propperties + userId + expiration time + signature
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour expiration
                .signWith(key)
                .compact();
    }

    public static Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Long extractUserId(String token) {
        try {
            String userIdStr = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
            return Long.parseLong(userIdStr);
        } catch (Exception e) {
            return null;
        }
    }
}
