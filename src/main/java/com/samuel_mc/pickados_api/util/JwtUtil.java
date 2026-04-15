package com.samuel_mc.pickados_api.util;

import com.samuel_mc.pickados_api.entity.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    public static final String EMAIL_VERIFICATION_PURPOSE = "email_verification";
    public static final String PASSWORD_RESET_PURPOSE = "password_reset";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private Key key;

    @PostConstruct
    public void init() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("jwt.secret debe configurarse mediante JWT_SECRET y ser Base64 válido de al menos 32 bytes.");
        }
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secret);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("jwt.secret debe estar codificado en Base64.", ex);
        }
        if (keyBytes.length < 32) {
            throw new IllegalStateException("jwt.secret debe tener al menos 32 bytes una vez decodificado.");
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(CustomUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities().stream().findFirst().map(GrantedAuthority::getAuthority).orElse(""));
        claims.put("userId", userDetails.getId());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    public Claims getAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public String generateEmailVerificationToken(String email) {
        long emailTokenExpirationMs = 24 * 60 * 60 * 1000L;
        return generateScopedToken(email, EMAIL_VERIFICATION_PURPOSE, emailTokenExpirationMs);
    }

    public String generatePasswordResetToken(String email) {
        long passwordResetExpirationMs = 60 * 60 * 1000L;
        return generateScopedToken(email, PASSWORD_RESET_PURPOSE, passwordResetExpirationMs);
    }

    public boolean validateTokenForPurpose(String token, String expectedPurpose) {
        try {
            Claims claims = getAllClaims(token);
            String purpose = claims.get("purpose", String.class);
            return expectedPurpose.equals(purpose);
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        return getUsernameFromToken(token);
    }

    private String generateScopedToken(String email, String purpose, long expirationTimeMs) {
        return Jwts.builder()
                .setSubject(email)
                .claim("purpose", purpose)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
