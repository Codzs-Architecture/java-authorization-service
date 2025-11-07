package com.codzs.entity.security;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * MongoDB Document representing system users.
 * This entity stores user authentication information.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Document(collection = "user")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    @NotBlank(message = "Username is required")
    @Size(max = 255, message = "Username must not exceed 255 characters")
    private String username;

    @Indexed(unique = true)
    @NotBlank(message = "Email is required")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(max = 500, message = "Password must not exceed 500 characters")
    private String password;

    @NotNull(message = "Enabled status is required")
    private Boolean enabled;

    // Constructors
    public User() {
        this.enabled = true;
    }

    public User(String email, String password) {
        this();
        this.username = email; // Use email as username
        this.email = email;
        this.password = password;
    }

    public User(String email, String password, boolean enabled) {
        this.username = email; // Use email as username
        this.email = email;
        this.password = password;
        this.enabled = enabled;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { 
        this.email = email;
        this.username = email; // Keep username and email in sync
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public boolean isEnabled() { return Boolean.TRUE.equals(enabled); }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}