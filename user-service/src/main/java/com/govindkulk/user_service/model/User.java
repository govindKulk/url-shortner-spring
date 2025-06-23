package com.govindkulk.user_service.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * User Entity Class
 * 
 * This class represents a user in our application and implements Spring Security's UserDetails interface.
 * It maps to the 'users' table in the database and handles user authentication and authorization.
 * 
 * Key Features:
 * - Implements UserDetails for Spring Security integration
 * - Uses JPA annotations for database mapping
 * - Manages roles as a collection of strings
 * - Uses FetchType.EAGER for roles to avoid LazyInitializationException
 * 
 * Database Schema:
 * - users table: id, username, password, email, enabled
 * - user_roles table: user_id, roles (many-to-many relationship)
 */
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;


    // Spring Security fields

    // Enabled field is used to check if the user is enabled or disabled
    @Column(nullable = false)
    private boolean enabled = true;

    // Account Non Expired field is used to check if the user's account has expired
    @Column(nullable = false)
    private boolean accountNonExpired = true;

    // Account Non Locked field is used to check if the user's account is locked or unlocked
    @Column(nullable = false)
    private boolean accountNonLocked = true;

    // Credentials Non Expired field is used to check if the user's credentials have expired
    @Column(nullable = false)
    private boolean credentialsNonExpired = true;

    // OAuth2 fields
    @Column(name = "oauth2_provider")
    private String oauth2Provider; // "google", "github", etc.

    @Column(name = "oauth2_id")
    private String oauth2Id; // Provider's user ID

    @Column(name = "name")
    private String name; // Full name from OAuth2

    @Column(name = "picture_url")
    private String pictureUrl; // Profile picture URL

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles;

    // Default constructor required by JPA
    public User() {}

    // Constructor for creating users
    public User(String username, String password, String email, Set<String> roles) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = roles;
        this.enabled = true;
    }

    // OAuth2 constructor
    public User(String email, String oauth2Provider, String oauth2Id, String name, String pictureUrl) {
        this.username = email; // Use email as username for OAuth2 users
        this.password = ""; // No password for OAuth2 users
        this.email = email;
        this.oauth2Provider = oauth2Provider;
        this.oauth2Id = oauth2Id;
        this.name = name;
        this.pictureUrl = pictureUrl;
        this.roles = new HashSet<>();
        this.roles.add("USER");
        this.enabled = true;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    // OAuth2 getters and setters
    public String getOauth2Provider() {
        return oauth2Provider;
    }

    public void setOauth2Provider(String oauth2Provider) {
        this.oauth2Provider = oauth2Provider;
    }

    public String getOauth2Id() {
        return oauth2Id;
    }

    public void setOauth2Id(String oauth2Id) {
        this.oauth2Id = oauth2Id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    // Helper methods
    public boolean isOauth2User() {
        return oauth2Provider != null && !oauth2Provider.isEmpty();
    }

    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    public boolean hasAnyRole(String... roles) {
        if (this.roles == null) return false;
        for (String role : roles) {
            if (this.roles.contains(role)) return true;
        }
        return false;
    }

    public void addRole(String role) {
        if (roles == null) {
            roles = new HashSet<>();
        }
        roles.add(role);
    }

    public void removeRole(String role) {
        if (roles != null) {
            roles.remove(role);
        }
    }

    // UserDetails Interface Implementation

    /**
     * Returns the authorities granted to the user.
     * Converts our role strings to Spring Security GrantedAuthority objects.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

    /**
     * Returns the password used to authenticate the user.
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Returns the username used to authenticate the user.
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Indicates whether the user's account has expired.
     * For this implementation, we always return true (no expiration).
     */
    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    /**
     * Indicates whether the user is locked or unlocked.
     * For this implementation, we always return true (not locked).
     */
    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    /**
     * Indicates whether the user's credentials (password) has expired.
     * For this implementation, we always return true (no expiration).
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    /**
     * Indicates whether the user is enabled or disabled.
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", enabled=" + enabled +
                ", roles=" + roles +
                ", oauth2Provider='" + oauth2Provider + '\'' +
                ", name='" + name + '\'' +
                '}';
    }


    // 😊 just for fun builder pattern for user
    public static class UserBuilder {

        private User user;

        public UserBuilder(){
            user = new User();
        }


        public UserBuilder setUsername(String username) {
            user.username = username;
            return this;
        }

        public UserBuilder setPassword(String password) {
            user.password = password;
            return this;
        }

        public UserBuilder setEmail(String email) {
            user.email = email;
            return this;
        }

        public UserBuilder setEnabled(boolean enabled) {
            user.enabled = enabled;
            return this;
        }

        public UserBuilder setRoles(Set<String> roles) {
            user.roles = roles;
            return this;
        }

        public UserBuilder setOauth2Provider(String oauth2Provider) {
            user.oauth2Provider = oauth2Provider;
            return this;
        }

        public UserBuilder setOauth2Id(String oauth2Id) {
            user.oauth2Id = oauth2Id;
            return this;
        }

        public UserBuilder setName(String name) {
            user.name = name;
            return this;
        }

        public UserBuilder setPictureUrl(String pictureUrl) {
            user.pictureUrl = pictureUrl;
            return this;
        }

        public User build() {
            return user;
        }
    }


    public static UserBuilder builder() {
        return new UserBuilder();
    }
}