package com.codzs.entity.oauth2;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * MongoDB Document representing OAuth2 registered clients.
 * This entity stores client registration information for OAuth2/OIDC flows.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Document(collection = "oauth2_registered_client")
public class OAuth2RegisteredClient {

    @Id
    private String id;

    @Indexed(unique = true)
    @NotBlank(message = "Client ID is required")
    @Size(max = 100, message = "Client ID must not exceed 100 characters")
    private String clientId;

    @NotNull(message = "Client ID issued at timestamp is required")
    private LocalDateTime clientIdIssuedAt;

    @Size(max = 200, message = "Client secret must not exceed 200 characters")
    private String clientSecret;

    private LocalDateTime clientSecretExpiresAt;

    @NotBlank(message = "Client name is required")
    @Size(max = 200, message = "Client name must not exceed 200 characters")
    private String clientName;

    @NotBlank(message = "Client authentication methods are required")
    @Size(max = 1000, message = "Client authentication methods must not exceed 1000 characters")
    private String clientAuthenticationMethods;

    @NotBlank(message = "Authorization grant types are required")
    @Size(max = 1000, message = "Authorization grant types must not exceed 1000 characters")
    private String authorizationGrantTypes;

    @Size(max = 1000, message = "Redirect URIs must not exceed 1000 characters")
    private String redirectUris;

    @Size(max = 1000, message = "Post logout redirect URIs must not exceed 1000 characters")
    private String postLogoutRedirectUris;

    @NotBlank(message = "Scopes are required")
    @Size(max = 1000, message = "Scopes must not exceed 1000 characters")
    private String scopes;

    @NotBlank(message = "Client settings are required")
    @Size(max = 2000, message = "Client settings must not exceed 2000 characters")
    private String clientSettings;

    @NotBlank(message = "Token settings are required")
    @Size(max = 2000, message = "Token settings must not exceed 2000 characters")
    private String tokenSettings;

    // Constructors
    public OAuth2RegisteredClient() {
        this.clientIdIssuedAt = LocalDateTime.now();
    }

    public OAuth2RegisteredClient(String clientId, String clientName) {
        this();
        this.clientId = clientId;
        this.clientName = clientName;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public LocalDateTime getClientIdIssuedAt() { return clientIdIssuedAt; }
    public void setClientIdIssuedAt(LocalDateTime clientIdIssuedAt) { this.clientIdIssuedAt = clientIdIssuedAt; }

    public String getClientSecret() { return clientSecret; }
    public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }

    public LocalDateTime getClientSecretExpiresAt() { return clientSecretExpiresAt; }
    public void setClientSecretExpiresAt(LocalDateTime clientSecretExpiresAt) { this.clientSecretExpiresAt = clientSecretExpiresAt; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getClientAuthenticationMethods() { return clientAuthenticationMethods; }
    public void setClientAuthenticationMethods(String clientAuthenticationMethods) { this.clientAuthenticationMethods = clientAuthenticationMethods; }

    public String getAuthorizationGrantTypes() { return authorizationGrantTypes; }
    public void setAuthorizationGrantTypes(String authorizationGrantTypes) { this.authorizationGrantTypes = authorizationGrantTypes; }

    public String getRedirectUris() { return redirectUris; }
    public void setRedirectUris(String redirectUris) { this.redirectUris = redirectUris; }

    public String getPostLogoutRedirectUris() { return postLogoutRedirectUris; }
    public void setPostLogoutRedirectUris(String postLogoutRedirectUris) { this.postLogoutRedirectUris = postLogoutRedirectUris; }

    public String getScopes() { return scopes; }
    public void setScopes(String scopes) { this.scopes = scopes; }

    public String getClientSettings() { return clientSettings; }
    public void setClientSettings(String clientSettings) { this.clientSettings = clientSettings; }

    public String getTokenSettings() { return tokenSettings; }
    public void setTokenSettings(String tokenSettings) { this.tokenSettings = tokenSettings; }

    @Override
    public String toString() {
        return "OAuth2RegisteredClient{" +
                "id='" + id + '\'' +
                ", clientId='" + clientId + '\'' +
                ", clientName='" + clientName + '\'' +
                ", clientIdIssuedAt=" + clientIdIssuedAt +
                '}';
    }
}