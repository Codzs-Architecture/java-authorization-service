package com.codzs.repository.security;

import com.codzs.entity.security.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User MongoDB documents.
 * Provides methods for managing system users.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Find a user by username.
     * 
     * @param username the username
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Find a user by email.
     * 
     * @param email the email address
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a username exists.
     * 
     * @param username the username to check
     * @return true if the username exists
     */
    boolean existsByUsername(String username);

    /**
     * Find all enabled users.
     * 
     * @return list of enabled users
     */
    List<User> findByEnabledTrue();

    /**
     * Find all disabled users.
     * 
     * @return list of disabled users
     */
    List<User> findByEnabledFalse();

    /**
     * Find users by username pattern (case-insensitive partial match).
     * 
     * @param username the username pattern to search for
     * @return list of matching users
     */
    @Query("{ 'username': { $regex: ?0, $options: 'i' } }")
    List<User> findByUsernameContainingIgnoreCase(String username);

    /**
     * Count enabled users.
     * 
     * @return count of enabled users
     */
    long countByEnabledTrue();

    /**
     * Count disabled users.
     * 
     * @return count of disabled users
     */
    long countByEnabledFalse();

    /**
     * Delete a user by username.
     * 
     * @param username the username
     * @return number of deleted records
     */
    long deleteByUsername(String username);
}