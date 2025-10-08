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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * JDBC-based UserDetailsService implementation.
 * This class is now deprecated as MongoDB-based UserDetailsService is used instead.
 * 
 * @author Nitin Khaitan
 * @since 1.1
 * @deprecated Use MongoUserDetailsService instead
 */
@Deprecated
public class JdbcUserDetailsService implements UserDetailsService {

    /**
     * This method is deprecated. Use MongoUserDetailsService instead.
     * 
     * @param username the username to load
     * @return UserDetails object containing user information and authorities
     * @throws UsernameNotFoundException if user is not found or cannot be loaded
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        throw new UnsupportedOperationException("JdbcUserDetailsService is deprecated. Use MongoUserDetailsService instead.");
    }
}