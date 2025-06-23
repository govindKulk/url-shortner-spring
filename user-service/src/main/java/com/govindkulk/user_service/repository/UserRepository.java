package com.govindkulk.user_service.repository;

import com.govindkulk.user_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by their username.
     * 
     * Spring Data JPA automatically creates a query like:
     * SELECT * FROM users WHERE username = ?
     * 
     * @param username the username to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     * Spring Data JPA automatically creates the implementation
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by OAuth2 provider and OAuth2 ID
     */
    Optional<User> findByOauth2ProviderAndOauth2Id(String provider, String oauth2Id);

    /**
     * Find user by email and OAuth2 provider
     */
    Optional<User> findByEmailAndOauth2Provider(String email, String provider);

    /**
     * Check if a user exists with the given username.
     * 
     * @param username the username to check
     * @return true if user exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Check if a user exists with the given email.
     * 
     * @param email the email to check
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Check if a user exists with the given OAuth2 provider and ID.
     */
    boolean existsByOauth2ProviderAndOauth2Id(String provider, String oauth2Id);
} 