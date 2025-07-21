/*
 * Copyright 2020-2025 Nitin Khaitan.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.codzs.service.user;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * JDBC-based UserDetailsService implementation.
 * Fetches user details from the database using the 'users' and 'authorities' tables.
 * 
 * This service handles user authentication by loading user credentials and authorities
 * from the database and converting them into Spring Security UserDetails objects.
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
@Service
public class JdbcUserDetailsService implements UserDetailsService {

    private final Log logger = LogFactory.getLog(getClass());
    
    private final JdbcTemplate jdbcTemplate;
    // SQL queries for user and authority retrieval
    private static final String USER_QUERY = 
        "SELECT username, password, enabled FROM users WHERE username = ?";
    private static final String AUTHORITIES_QUERY = 
        "SELECT authority FROM authorities WHERE username = ?";

    /**
     * Constructor for JdbcUserDetailsService.
     * 
     * @param jdbcTemplate the JDBC template for database operations
     */
    public JdbcUserDetailsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Loads user details by username from the database.
     * 
     * @param username the username to load
     * @return UserDetails object containing user information and authorities
     * @throws UsernameNotFoundException if user is not found or cannot be loaded
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (logger.isDebugEnabled()) {
            logger.debug("Loading user details for username: " + username);
        }

        try {
            UserDetails user = jdbcTemplate.queryForObject(USER_QUERY, (rs, rowNum) -> {
                String dbUsername = rs.getString("username");
                String rawPassword = rs.getString("password");
                boolean enabled = rs.getBoolean("enabled");
                
                // Load user authorities
                List<SimpleGrantedAuthority> grantedAuthorities = loadUserAuthorities(dbUsername);
                
                // Create UserDetails with proper password handling
                return User.builder()
                    .username(dbUsername)
                    .password(rawPassword) // Keep the original password format from database
                    .disabled(!enabled)
                    .authorities(grantedAuthorities)
                    .build();
            }, username);
            
            if (user == null) {
                throw new UsernameNotFoundException("User not found: " + username);
            }
            
            if (logger.isDebugEnabled()) {
                logger.debug("Successfully loaded user: " + username + " with " + 
                    user.getAuthorities().size() + " authorities");
            }
            
            return user;
            
        } catch (Exception e) {
            logger.warn("Error loading user details for username: " + username, e);
            throw new UsernameNotFoundException("Error loading user: " + username, e);
        }
    }

    /**
     * Loads user authorities from the database.
     * 
     * @param username the username to load authorities for
     * @return list of granted authorities
     */
    private List<SimpleGrantedAuthority> loadUserAuthorities(String username) {
        List<String> authorities = jdbcTemplate.queryForList(AUTHORITIES_QUERY, String.class, username);
        
        return authorities.stream()
            .map(this::normalizeAuthority)
            .map(SimpleGrantedAuthority::new)
            .toList();
    }

    /**
     * Normalizes authority names to ensure they follow Spring Security conventions.
     * Ensures authority starts with ROLE_ prefix if it doesn't already have it.
     * 
     * @param authority the authority to normalize
     * @return normalized authority name
     */
    private String normalizeAuthority(String authority) {
        if (authority != null && !authority.startsWith("ROLE_")) {
            return "ROLE_" + authority;
        }
        return authority;
    }
}