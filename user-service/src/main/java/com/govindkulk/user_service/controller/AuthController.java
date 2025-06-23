package com.govindkulk.user_service.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.govindkulk.user_service.dto.AuthResponse;
import com.govindkulk.user_service.dto.LoginRequest;
import com.govindkulk.user_service.dto.RegisterRequest;
import com.govindkulk.user_service.dto.TokenResponse;
import com.govindkulk.user_service.exception.UnauthorizedException;
import com.govindkulk.user_service.service.AuthService;

/**
 * Authentication Controller
 * Handles login, registration, and token refresh endpoints
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Allow CORS for development
public class AuthController {

    
    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    /**
     * Register a new user
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<? extends TokenResponse> register(@RequestBody RegisterRequest request) {

       
            TokenResponse response = authService.register(request);


            return ResponseEntity.created(URI.create("/api/auth/register")).body(response);
         
    }

    /**
     * Login a user
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get current user info from JWT token
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser(@RequestHeader("Authorization") String authHeader) {

        System.out.println("AuthHeader: " + authHeader);
        
        AuthResponse response = authService.getCurrentUser();
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Validate JWT token
     * POST /api/auth/validate
     */
    @PostMapping("/validate")
    public ResponseEntity<AuthResponse> validateToken(@RequestHeader("Authorization") String authHeader) {

        
        AuthResponse response = authService.getCurrentUser();
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(AuthResponse.success("Token is valid", response.getUsername(), response.getRole(), response.getToken()));
        } else {
            return ResponseEntity.status(401).body(response);
        }
    }

    /**
     * Token refresh endpoint
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }

        String refreshToken = authHeader.substring(7);
        TokenResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    /**
     * Logout endpoint (client-side token removal)
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // In a stateless JWT system, logout is handled client-side
        // You could implement a token blacklist here for additional security
        return ResponseEntity.ok("Logged out successfully");
    }
}
