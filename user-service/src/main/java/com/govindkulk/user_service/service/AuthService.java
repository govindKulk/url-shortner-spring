package com.govindkulk.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.govindkulk.user_service.config.JwtConfig;
import com.govindkulk.user_service.dto.AuthResponse;
import com.govindkulk.user_service.dto.LoginRequest;
import com.govindkulk.user_service.dto.RegisterRequest;
import com.govindkulk.user_service.dto.TokenResponse;
import com.govindkulk.user_service.exception.UnauthorizedException;
import com.govindkulk.user_service.exception.UserAlreadyExistsException;
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

@Service
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
            throw new UserAlreadyExistsException("User already exists");
        }

        // check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
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

        Map<String, String> tokens = jwtTokenUtil.generateTokenPair(userDetails, user.getId());
        return new TokenResponse(
                tokens.get("accessToken"),
                tokens.get("refreshToken"),
                jwtConfig.getAccessTokenExpiration() / 1000);

    }

    public TokenResponse login(LoginRequest request) {

       Optional<User> user = userRepository.findByUsername(request.getUsername());
       if (user.isEmpty()) {
        throw new UsernameNotFoundException("User not found");
       }

        // check if password is correct

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Map<String, String> tokens = jwtTokenUtil.generateTokenPair(userDetails, user.get().getId());
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
                    
            throw new UnauthorizedException("No user logged in from service.");
        }

        String username = authentication.getName();

        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("Username not found.");
        }

        User user = userOpt.get();
        // get first role
        String role = user.getRoles().iterator().next();

        // Generate tokens
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());
        Map<String, String> tokens = jwtTokenUtil.generateTokenPair(userDetails, user.getId());

        return AuthResponse.success(
                "Current user info",
                user.getUsername(),
                role,
                tokens.get("accessToken"));
    }

    public TokenResponse refreshToken(String refreshToken) {
        // Validate refresh token
        if (!jwtTokenUtil.validateRefreshToken(refreshToken)) {
            throw new UnauthorizedException("Unauthorized Refresh Token");
        }

        // Extract username and load user details
        String username = jwtTokenUtil.extractUsername(refreshToken);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        // Generate new token pair
        Map<String, String> tokens = jwtTokenUtil.generateTokenPair(userDetails, user.get().getId());

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
            Map<String, String> tokens = jwtTokenUtil.generateTokenPair(userDetails, user.getId());

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
