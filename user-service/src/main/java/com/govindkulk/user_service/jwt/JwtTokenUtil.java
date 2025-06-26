package com.govindkulk.user_service.jwt;

import com.govindkulk.user_service.config.JwtConfig;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Enhanced JWT Token Utility Class
 * Handles both access and refresh token generation, validation, and extraction
 */
@Component
public class JwtTokenUtil {

    @Autowired
    private JwtConfig jwtConfig;

    /**
     * Generate access token for a user
     */
    public String generateAccessToken(UserDetails userDetails , Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "ACCESS");
        claims.put("userid", userId);
        
        // use userId for subject
        return createToken(claims, userDetails.getUsername(), jwtConfig.getAccessTokenExpiration());
    }
    
    /**
     * Generate refresh token for a user
     */
    public String generateRefreshToken(UserDetails userDetails, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "REFRESH");
        claims.put("userid", userId);
        return createToken(claims, userDetails.getUsername(), jwtConfig.getRefreshTokenExpiration());
    }

    /**
     * Generate both access and refresh tokens
     */
    public Map<String, String> generateTokenPair(UserDetails userDetails, Long userId) {
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", generateAccessToken(userDetails, userId));
        tokens.put("refreshToken", generateRefreshToken(userDetails, userId));
        return tokens;
    }

    // /**
    //  * Generate a JWT token for a user (legacy method)
    //  */
    // public String generateToken(UserDetails userDetails) {
    //     Map<String, Object> claims = new HashMap<>();
    //     return createToken(claims, userDetails.getUsername(), jwtConfig.getAccessTokenExpiration());
    // }

    // /**
    //  * Generate a JWT token with custom claims (legacy method)
    //  */
    // public String generateToken(String username, String role) {
    //     Map<String, Object> claims = new HashMap<>();
    //     claims.put("role", role);
    //     return createToken(claims, username, jwtConfig.getAccessTokenExpiration());
    // }

    /**
     * Create JWT token with claims, subject, and expiration
     */
    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(jwtConfig.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract username from JWT token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
        /* 
         * 
         * equivalent to
         * return extractClaim(token, (Claims claims) -> claims.getSubject())
         */
    }

    /**
     * Extract expiration date from JWT token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
        /* 
         * 
         * equivalent to
         * return extractClaim(token, (Claims claims) -> claims.getExpiration())
         */
    }

    /**
     * Extract token type (ACCESS or REFRESH)
     */
    public String extractTokenType(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("type", String.class);
    }

    /**
     * Extract a specific claim from JWT token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from JWT token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Get the signing key for JWT
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtConfig.getSecret().getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Check if token is expired
     */
    private Boolean isTokenExpired(String token) {

        // before is a method of Date class that checks if the date is before the given date
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validate JWT token for a specific user
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        // token is valid if the username is the same as the userDetails.getUsername() 
        // and the token is not expired
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Validate JWT token (without UserDetails)
     */
    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Validate refresh token specifically
     */
    public Boolean validateRefreshToken(String token) {
        try {
            if (!validateToken(token)) {
                return false;
            }
            
            String tokenType = extractTokenType(token);
            return "REFRESH".equals(tokenType);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extract role from JWT token
     */
    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }
} 


