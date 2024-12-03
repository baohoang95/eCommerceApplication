package com.example.demo.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.UserRepository;

/**
 * Implementation of the {@link UserDetailsService} interface for loading user-specific data.
 * Used by Spring Security to fetch user information during authentication.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    /**
     * Repository for accessing user data from the database.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Loads the user details by username.
     * <p>
     * This method retrieves the user from the database using the username and constructs
     * a {@link UserDetails} object containing the username, password, and authorities.
     * </p>
     *
     * @param username the username identifying the user whose data is required
     * @return a fully populated {@link UserDetails} object
     * @throws UsernameNotFoundException if no user is found with the given username
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch the user from the database
        User user = userRepository.findByUsername(username);

        // Throw an exception if the user is not found
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        // Return a UserDetails object with the user's information
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.emptyList() // No granted authorities assigned
        );
    }
}
