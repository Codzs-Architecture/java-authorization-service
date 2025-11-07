package com.codzs.service.user;

import com.codzs.entity.security.User;
import com.codzs.repository.security.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.util.Optional;

/**
 * Service implementation for User-related operations.
 * Provides methods for user management and authentication.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private static final String PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    private static final int DEFAULT_PASSWORD_LENGTH = 12;
    private final SecureRandom secureRandom = new SecureRandom();

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User findOrCreateUserByEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("Email address is required");
        }

        log.debug("Finding or creating user for email: {}", email);

        // Try to find existing user
        Optional<User> existingUser = userRepository.findByEmail(email.toLowerCase().trim());
        
        if (existingUser.isPresent()) {
            log.debug("Found existing user for email: {}", email);
            return existingUser.get();
        }

        // Create new user if not found
        log.info("Creating new user for email: {}", email);
        return createUserWithEmail(email);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return Optional.empty();
        }

        log.debug("Finding user by email: {}", email);
        return userRepository.findByEmail(email.toLowerCase().trim());
    }

    @Override
    @Transactional
    public User createUserWithEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("Email address is required");
        }

        String normalizedEmail = email.toLowerCase().trim();
        
        log.debug("Creating new user with email: {}", normalizedEmail);

        // Generate default password
        String defaultPassword = generateDefaultPassword();
        String encodedPassword = passwordEncoder.encode(defaultPassword);

        // Create new user (email will be used as username)
        User newUser = new User(normalizedEmail, encodedPassword, true);
        
        User savedUser = userRepository.save(newUser);
        
        log.info("Successfully created new user with ID: {} for email: {}", 
                savedUser.getId(), normalizedEmail);

        // Note: In a real application, you would typically:
        // 1. Send welcome email with temporary password
        // 2. Force password change on first login
        // 3. Log this action for audit purposes
        log.warn("Created user with generated password for email: {}. " +
                "Consider implementing secure password delivery mechanism.", normalizedEmail);
        
        return savedUser;
    }

    @Override
    public String generateDefaultPassword() {
        StringBuilder password = new StringBuilder(DEFAULT_PASSWORD_LENGTH);
        
        for (int i = 0; i < DEFAULT_PASSWORD_LENGTH; i++) {
            int randomIndex = secureRandom.nextInt(PASSWORD_CHARS.length());
            password.append(PASSWORD_CHARS.charAt(randomIndex));
        }
        
        return password.toString();
    }
}