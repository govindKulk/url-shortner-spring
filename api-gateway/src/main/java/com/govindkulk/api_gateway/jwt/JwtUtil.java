package com.govindkulk.api_gateway.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

@Component
public class JwtUtil {
    
    private final String secret;
    private final SecretKey secretKey;
    
    public JwtUtil(@Value("${jwt.secret:my-super-secret-jwt-key-that-is-at-least-32-characters-long}") String secret) {
        this.secret = secret;
        
        // Ensure the secret is at least 32 characters (256 bits)
        if (secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters long for HMAC-SHA256");
        }
        
        try {
            // Create the secret key once during initialization
            byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            this.secretKey = Keys.hmacShaKeyFor(keyBytes);
            System.out.println("JwtUtil initialized successfully with secret length: " + secret.length());
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize JWT secret key: " + e.getMessage(), e);
        }
    }
    
    private SecretKey getSigningKey() {
        return this.secretKey;
    }
    
    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
            System.out.println("Failed to extract username: " + e.getMessage());
            return null;
        }
    }
    
    public Long extractUserId(String token) {
        try {
            final Claims claims = extractAllClaims(token);
            return claims.get("userid", Long.class);
        } catch (Exception e) {
            System.out.println("Failed to extract userId: " + e.getMessage());
            return null;
        }
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JWT token: " + e.getMessage(), e);
        }
    }
    
    public Boolean validateToken(String token) {
        try {
            System.out.println("Validating token with secret length: " + secret.length());
            return !isTokenExpired(token);
        } catch (Exception e) {
            System.out.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }
    
    private Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new java.util.Date());
        } catch (Exception e) {
            System.out.println("Failed to check token expiration: " + e.getMessage());
            return true;
        }
    }
    
    private java.util.Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}