package com.govindkulk.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.govindkulk.user_service.model.User;
import com.govindkulk.user_service.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {


    // this is discouraged because it is not a good practice to use @Autowired for the final fields
    // and also it makes testing difficult
    // can use in legacy code

    // @Autowired
    // private UserRepository userRepository;

   
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username: " + username));

        // Log successful user loading (for debugging)
        System.out.println("Loaded user: " + user.getUsername() + " with roles: " + user.getRoles());

        // Return the user (our User entity implements UserDetails)
        return user;
    }

    // public User createUser(User user){
    //     return userRepository.save(user);
    // }

    // public boolean userExists(String username){
    //     return userRepository.findByUsername(username).isPresent();
    // }

    public User loadUserByEmail(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + email));
        return user;
    }

}
