package com.codzs.repository.security;

import com.codzs.entity.security.Authority;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Authority MongoDB documents.
 * Provides methods for managing user authorities (roles).
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Repository
public interface AuthorityRepository extends MongoRepository<Authority, String> {

    /**
     * Find all authorities for a specific username.
     * 
     * @param username the username
     * @return list of authorities for the user
     */
    List<Authority> findByUsername(String username);

    /**
     * Find all users with a specific authority.
     * 
     * @param authority the authority
     * @return list of authorities (containing usernames) with the authority
     */
    List<Authority> findByAuthority(String authority);

    /**
     * Find a specific authority assignment.
     * 
     * @param username the username
     * @param authority the authority
     * @return Optional containing the authority assignment if found
     */
    Optional<Authority> findByUsernameAndAuthority(String username, String authority);

    /**
     * Check if a user has a specific authority.
     * 
     * @param username the username
     * @param authority the authority
     * @return true if the user has the authority
     */
    boolean existsByUsernameAndAuthority(String username, String authority);

    /**
     * Delete all authorities for a specific username.
     * 
     * @param username the username
     * @return number of deleted records
     */
    long deleteByUsername(String username);

    /**
     * Delete a specific authority assignment.
     * 
     * @param username the username
     * @param authority the authority
     * @return number of deleted records
     */
    long deleteByUsernameAndAuthority(String username, String authority);

    /**
     * Delete all assignments of a specific authority.
     * 
     * @param authority the authority
     * @return number of deleted records
     */
    long deleteByAuthority(String authority);

    /**
     * Find all distinct authorities.
     * 
     * @return list of distinct authority names
     */
    @Query(value = "{}", fields = "{ 'authority': 1 }")
    List<String> findDistinctAuthorities();

    /**
     * Find all distinct usernames.
     * 
     * @return list of distinct usernames
     */
    @Query(value = "{}", fields = "{ 'username': 1 }")
    List<String> findDistinctUsernames();

    /**
     * Count authorities for a specific username.
     * 
     * @param username the username
     * @return count of authorities for the user
     */
    long countByUsername(String username);

    /**
     * Count users with a specific authority.
     * 
     * @param authority the authority
     * @return count of users with the authority
     */
    long countByAuthority(String authority);

    /**
     * Find users with multiple authorities.
     * 
     * @param authorities list of authorities to match
     * @return list of usernames having all specified authorities
     */
    @Query("{ 'authority': { $in: ?0 } }")
    List<Authority> findByAuthorityIn(List<String> authorities);
}