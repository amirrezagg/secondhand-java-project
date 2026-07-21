package ir.aut.secondhand.security;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

/**
 * Utility component for JWT (JSON Web Token) operations in the secondhand application.
 * Provides token generation, username extraction, and expiration validation.
 * Uses HMAC SHA-256 algorithm for token signing with a configured secret key.
 */
@Component
public class JwtUtil {

    @Value("${JWT_SECRET_KEY}")
    private String secretKeyString;

    private Key key;

    // Token validity period set to 24 hours
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000L;

    /**
     * Initializes the cryptographic key for token signing from the configured secret key string.
     * Called automatically after bean construction.
     */
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    /**
     * Generates a signed JWT token for the given username.
     * The token includes issue time and expiration time (24 hours).
     *
     * @param username the subject/username to encode in the token
     * @return the compact JWT token string
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts the username (subject claim) from a valid JWT token.
     *
     * @param token the JWT token string
     * @return the username embedded in the token
     */
    public String extractUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /**
     * Validates whether the given JWT token has expired.
     *
     * @param token the JWT token string
     * @return true if the token expiration time is before the current time; false otherwise
     */
    public boolean isTokenExpired(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration().before(new Date());
    }
}
