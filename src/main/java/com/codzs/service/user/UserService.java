package com.codzs.service.user;

import com.codzs.entity.security.User;

import java.util.Optional;

/**
 * Service interface for User-related operations.
 * Provides methods for user management and authentication.
 * 
 * @author Codzs Team
 * @since 1.0
 */
public interface UserService {

    /**
     * Finds or creates a user by email address.
     * If user exists, returns the existing user.
     * If user doesn't exist, creates a new user with a generated password.
     * 
     * @param email the email address to find or create user for
     * @return the found or created User
     */
    User findOrCreateUserByEmail(String email);

    /**
     * Finds a user by email address.
     * 
     * @param email the email address to search for
     * @return Optional containing the user if found
     */
    Optional<User> findUserByEmail(String email);

    /**
     * Creates a new user with the given email and a generated password.
     * 
     * @param email the email address for the new user
     * @return the created User
     */
    User createUserWithEmail(String email);

    /**
     * Generates a secure random password for new users.
     * 
     * @return generated password
     */
    String generateDefaultPassword();
}