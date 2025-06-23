package com.govindkulk.user_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT Configuration Properties
 * Enhanced for both access and refresh tokens
 */
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    
    private String secret;
    private long accessTokenExpiration = 900000;  // 15 minutes default
    private long refreshTokenExpiration = 604800000; // 7 days default
    private String issuer = "url-shortner-application";
    
    // Getters and Setters for JWT Config
    // populated from .env and Application.yaml
    public String getSecret() {
        return secret;
    }
    
    public void setSecret(String secret) {
        this.secret = secret;
    }
    
    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }
    
    public void setAccessTokenExpiration(long accessTokenExpiration) {
        this.accessTokenExpiration = accessTokenExpiration;
    }
    
    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }
    
    public void setRefreshTokenExpiration(long refreshTokenExpiration) {
        this.refreshTokenExpiration = refreshTokenExpiration;
    }
    
    public String getIssuer() {
        return issuer;
    }
    
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
    
    // Legacy method for backward compatibility
    public long getExpiration() {
        return accessTokenExpiration;
    }
    
    public void setExpiration(long expiration) {
        this.accessTokenExpiration = expiration;
    }
} 