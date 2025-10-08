package com.codzs.entity.security;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * MongoDB Document representing user authorities (roles).
 * This entity stores user role assignments.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Document(collection = "authority")
@CompoundIndexes({
    @CompoundIndex(name = "idx_authorities_username_authority", def = "{'username': 1, 'authority': 1}", unique = true)
})
public class Authority {

    @Id
    private String id;

    @Indexed
    @NotBlank(message = "Username is required")
    @Size(max = 50, message = "Username must not exceed 50 characters")
    private String username;

    @NotBlank(message = "Authority is required")
    @Size(max = 50, message = "Authority must not exceed 50 characters")
    private String authority;

    // Constructors
    public Authority() {}

    public Authority(String username, String authority) {
        this.username = username;
        this.authority = authority;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAuthority() { return authority; }
    public void setAuthority(String authority) { this.authority = authority; }

    @Override
    public String toString() {
        return "Authority{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", authority='" + authority + '\'' +
                '}';
    }
}