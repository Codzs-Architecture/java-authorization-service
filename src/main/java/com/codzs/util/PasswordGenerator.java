package com.codzs.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class to generate bcrypt passwords for testing purposes.
 * This class provides methods to generate bcrypt-encoded passwords
 * that can be used in database migrations or testing.
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
public class PasswordGenerator {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static void main(String[] args) {
        System.out.println("=== BCrypt Password Generator ===\n");
        
        // Generate bcrypt hash for common test passwords
        System.out.println("Test Passwords:");
        System.out.println("password -> " + encoder.encode("password"));
        System.out.println("admin -> " + encoder.encode("admin"));
        System.out.println("user123 -> " + encoder.encode("user123"));
        System.out.println("manager -> " + encoder.encode("manager"));
        
        System.out.println("\n=== Usage ===");
        System.out.println("Use these hashes in your database migrations.");
        System.out.println("Example: INSERT INTO users VALUES ('username', '{bcrypt}$2a$10$...', 1);");
    }

    /**
     * Generate a bcrypt hash for the given password.
     * 
     * @param password the plain text password
     * @return the bcrypt-encoded password
     */
    public static String encodePassword(String password) {
        return encoder.encode(password);
    }

    /**
     * Check if a plain text password matches a bcrypt hash.
     * 
     * @param plainPassword the plain text password
     * @param encodedPassword the bcrypt-encoded password
     * @return true if the passwords match
     */
    public static boolean matches(String plainPassword, String encodedPassword) {
        return encoder.matches(plainPassword, encodedPassword);
    }
} 