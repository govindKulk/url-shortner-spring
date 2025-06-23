package com.govindkulk.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.govindkulk.user_service.config.JwtConfig;
import com.govindkulk.user_service.dto.AuthResponse;
import com.govindkulk.user_service.dto.LoginRequest;
import com.govindkulk.user_service.dto.RegisterRequest;
import com.govindkulk.user_service.dto.TokenResponse;
import com.govindkulk.user_service.jwt.JwtTokenUtil;
import com.govindkulk.user_service.model.User;
import com.govindkulk.user_service.repository.UserRepository;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/*
 * auth service for authentication business logic
 * customuserdetailservice is for spring security 
 */
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    public TokenResponse register(RegisterRequest request) {

        // check if user already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("User already exists");
        }

        // check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // just for fun ive used custom builder pattern for user
        // which is not necessary here
        User user = User.builder()
                .setUsername(request.getUsername())
                .setPassword(passwordEncoder.encode(request.getPassword()))
                .setEmail(request.getEmail())
                .setRoles(new HashSet<>(Set.of("ROLE_USER")))
                .build();

        userRepository.save(user);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getUsername());

        Map<String, String> tokens = jwtTokenUtil.generateTokenPair(userDetails);
        return new TokenResponse(
                tokens.get("accessToken"),
                tokens.get("refreshToken"),
                jwtConfig.getAccessTokenExpiration() / 1000);

    }

    public TokenResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Map<String, String> tokens = jwtTokenUtil.generateTokenPair(userDetails);
        return new TokenResponse(
                tokens.get("accessToken"),
                tokens.get("refreshToken"),
                jwtConfig.getAccessTokenExpiration() / 1000);

    }

    /**
     * Get current user info
     */
    public AuthResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if user is authenticated and not anonymous
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getName())) {
            return AuthResponse.error("No user logged in");
        }

        String username = authentication.getName();

        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            return AuthResponse.error("User not found");
        }

        User user = userOpt.get();
        // get first role
        String role = user.getRoles().iterator().next();

        // Generate tokens
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());
        Map<String, String> tokens = jwtTokenUtil.generateTokenPair(userDetails);

        return AuthResponse.success(
                "Current user info",
                user.getUsername(),
                role,
                tokens.get("accessToken"));
    }

    public TokenResponse refreshToken(String refreshToken) {
        // Validate refresh token
        if (!jwtTokenUtil.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // Extract username and load user details
        String username = jwtTokenUtil.extractUsername(refreshToken);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // Generate new token pair
        Map<String, String> tokens = jwtTokenUtil.generateTokenPair(userDetails);

        return new TokenResponse(
                tokens.get("accessToken"),
                tokens.get("refreshToken"),
                jwtConfig.getAccessTokenExpiration() / 1000);
    }

    /**
     * Login a user with email
     */
    public AuthResponse loginWithEmail(String email, String password) {
        try {
            // First, find user by email
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                return AuthResponse.error("User not found with this email");
            }

            User user = userOpt.get();

            // Use username for authentication
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            password));

            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get the first role
            String role = user.getRoles().iterator().next();

            // Generate tokens
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());
            Map<String, String> tokens = jwtTokenUtil.generateTokenPair(userDetails);

            return AuthResponse.success(
                    "Login successful",
                    user.getUsername(),
                    role,
                    tokens.get("accessToken"));

        } catch (Exception e) {
            return AuthResponse.error("Invalid email or password");
        }
    }

}
