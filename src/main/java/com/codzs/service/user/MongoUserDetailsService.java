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

import com.codzs.entity.security.User;
import com.codzs.entity.security.Authority;
import com.codzs.repository.security.UserRepository;
import com.codzs.repository.security.AuthorityRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * MongoDB-based UserDetailsService implementation.
 * Fetches user details from MongoDB using the 'users' and 'authorities' collections.
 * 
 * This service handles user authentication by loading user credentials and authorities
 * from MongoDB and converting them into Spring Security UserDetails objects.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Service
public class MongoUserDetailsService implements UserDetailsService {

    private final Log logger = LogFactory.getLog(getClass());
    
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    /**
     * Constructor for MongoUserDetailsService.
     * 
     * @param userRepository the user repository for MongoDB operations
     * @param authorityRepository the authority repository for MongoDB operations
     */
    public MongoUserDetailsService(UserRepository userRepository, AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
    }

    /**
     * Loads user details by username from MongoDB.
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
            Optional<User> userOptional = userRepository.findByUsername(username);
            
            if (userOptional.isEmpty()) {
                throw new UsernameNotFoundException("User not found: " + username);
            }
            
            User user = userOptional.get();
            
            // Load user authorities
            List<SimpleGrantedAuthority> grantedAuthorities = loadUserAuthorities(username);
            
            // Create UserDetails with proper password handling
            UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword()) // Keep the original password format from database
                    .disabled(!user.isEnabled())
                    .authorities(grantedAuthorities)
                    .build();
            
            if (logger.isDebugEnabled()) {
                logger.debug("Successfully loaded user: " + username + " with " + 
                    userDetails.getAuthorities().size() + " authorities");
            }
            
            return userDetails;
            
        } catch (Exception e) {
            logger.warn("Error loading user details for username: " + username, e);
            throw new UsernameNotFoundException("Error loading user: " + username, e);
        }
    }

    /**
     * Loads user authorities from MongoDB.
     * 
     * @param username the username to load authorities for
     * @return list of granted authorities
     */
    private List<SimpleGrantedAuthority> loadUserAuthorities(String username) {
        List<Authority> authorities = authorityRepository.findByUsername(username);
        
        return authorities.stream()
                .map(authority -> authority.getAuthority())
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