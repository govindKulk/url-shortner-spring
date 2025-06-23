package com.govindkulk.user_service.dto;

public class AuthResponse {
    private String message;
    private String username;
    private String role;
    private boolean success;
    private String token; // Added for JWT

    // Default constructor
    public AuthResponse() {}

    // Constructor for success response
    public AuthResponse(String message, String username, String role, boolean success, String token) {
        this.message = message;
        this.username = username;
        this.role = role;
        this.success = success;
    }

    // Static factory methods for common responses
    public static AuthResponse success(String message, String username, String role, String token) {
        return new AuthResponse(message, username, role, true, token);
    }

    public static AuthResponse error(String message) {
        return new AuthResponse(message, null, null, false, null);
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
} 