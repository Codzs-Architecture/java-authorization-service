package com.codzs.entity.oauth2;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * MongoDB Document representing OAuth2 authorization.
 * This entity stores OAuth2 authorization details including tokens and codes.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Document(collection = "oauth2_authorization")
public class OAuth2Authorization {

    @Id
    private String id;

    @Indexed
    @NotBlank(message = "Registered client ID is required")
    @Size(max = 100, message = "Registered client ID must not exceed 100 characters")
    private String registeredClientId;

    @Indexed
    @NotBlank(message = "Principal name is required")
    @Size(max = 200, message = "Principal name must not exceed 200 characters")
    private String principalName;

    @NotBlank(message = "Authorization grant type is required")
    @Size(max = 100, message = "Authorization grant type must not exceed 100 characters")
    private String authorizationGrantType;

    @Size(max = 1000, message = "Authorized scopes must not exceed 1000 characters")
    private String authorizedScopes;

    private String attributes; // JSON string for MongoDB

    @Size(max = 500, message = "State must not exceed 500 characters")
    private String state;

    // Authorization Code
    private String authorizationCodeValue;
    private LocalDateTime authorizationCodeIssuedAt;
    private LocalDateTime authorizationCodeExpiresAt;
    private String authorizationCodeMetadata;

    // Access Token
    private String accessTokenValue;
    private LocalDateTime accessTokenIssuedAt;
    private LocalDateTime accessTokenExpiresAt;
    private String accessTokenMetadata;
    @Size(max = 100, message = "Access token type must not exceed 100 characters")
    private String accessTokenType;
    @Size(max = 1000, message = "Access token scopes must not exceed 1000 characters")
    private String accessTokenScopes;

    // OIDC ID Token
    private String oidcIdTokenValue;
    private LocalDateTime oidcIdTokenIssuedAt;
    private LocalDateTime oidcIdTokenExpiresAt;
    private String oidcIdTokenMetadata;
    private String oidcIdTokenClaims;

    // Refresh Token
    private String refreshTokenValue;
    private LocalDateTime refreshTokenIssuedAt;
    private LocalDateTime refreshTokenExpiresAt;
    private String refreshTokenMetadata;

    // User Code (for device flow)
    private String userCodeValue;
    private LocalDateTime userCodeIssuedAt;
    private LocalDateTime userCodeExpiresAt;
    private String userCodeMetadata;

    // Device Code (for device flow)
    private String deviceCodeValue;
    private LocalDateTime deviceCodeIssuedAt;
    private LocalDateTime deviceCodeExpiresAt;
    private String deviceCodeMetadata;

    // Constructors
    public OAuth2Authorization() {}

    public OAuth2Authorization(String registeredClientId, String principalName, String authorizationGrantType) {
        this.registeredClientId = registeredClientId;
        this.principalName = principalName;
        this.authorizationGrantType = authorizationGrantType;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRegisteredClientId() { return registeredClientId; }
    public void setRegisteredClientId(String registeredClientId) { this.registeredClientId = registeredClientId; }

    public String getPrincipalName() { return principalName; }
    public void setPrincipalName(String principalName) { this.principalName = principalName; }

    public String getAuthorizationGrantType() { return authorizationGrantType; }
    public void setAuthorizationGrantType(String authorizationGrantType) { this.authorizationGrantType = authorizationGrantType; }

    public String getAuthorizedScopes() { return authorizedScopes; }
    public void setAuthorizedScopes(String authorizedScopes) { this.authorizedScopes = authorizedScopes; }

    public String getAttributes() { return attributes; }
    public void setAttributes(String attributes) { this.attributes = attributes; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    // Authorization Code getters/setters
    public String getAuthorizationCodeValue() { return authorizationCodeValue; }
    public void setAuthorizationCodeValue(String authorizationCodeValue) { this.authorizationCodeValue = authorizationCodeValue; }

    public LocalDateTime getAuthorizationCodeIssuedAt() { return authorizationCodeIssuedAt; }
    public void setAuthorizationCodeIssuedAt(LocalDateTime authorizationCodeIssuedAt) { this.authorizationCodeIssuedAt = authorizationCodeIssuedAt; }

    public LocalDateTime getAuthorizationCodeExpiresAt() { return authorizationCodeExpiresAt; }
    public void setAuthorizationCodeExpiresAt(LocalDateTime authorizationCodeExpiresAt) { this.authorizationCodeExpiresAt = authorizationCodeExpiresAt; }

    public String getAuthorizationCodeMetadata() { return authorizationCodeMetadata; }
    public void setAuthorizationCodeMetadata(String authorizationCodeMetadata) { this.authorizationCodeMetadata = authorizationCodeMetadata; }

    // Access Token getters/setters
    public String getAccessTokenValue() { return accessTokenValue; }
    public void setAccessTokenValue(String accessTokenValue) { this.accessTokenValue = accessTokenValue; }

    public LocalDateTime getAccessTokenIssuedAt() { return accessTokenIssuedAt; }
    public void setAccessTokenIssuedAt(LocalDateTime accessTokenIssuedAt) { this.accessTokenIssuedAt = accessTokenIssuedAt; }

    public LocalDateTime getAccessTokenExpiresAt() { return accessTokenExpiresAt; }
    public void setAccessTokenExpiresAt(LocalDateTime accessTokenExpiresAt) { this.accessTokenExpiresAt = accessTokenExpiresAt; }

    public String getAccessTokenMetadata() { return accessTokenMetadata; }
    public void setAccessTokenMetadata(String accessTokenMetadata) { this.accessTokenMetadata = accessTokenMetadata; }

    public String getAccessTokenType() { return accessTokenType; }
    public void setAccessTokenType(String accessTokenType) { this.accessTokenType = accessTokenType; }

    public String getAccessTokenScopes() { return accessTokenScopes; }
    public void setAccessTokenScopes(String accessTokenScopes) { this.accessTokenScopes = accessTokenScopes; }

    // OIDC ID Token getters/setters
    public String getOidcIdTokenValue() { return oidcIdTokenValue; }
    public void setOidcIdTokenValue(String oidcIdTokenValue) { this.oidcIdTokenValue = oidcIdTokenValue; }

    public LocalDateTime getOidcIdTokenIssuedAt() { return oidcIdTokenIssuedAt; }
    public void setOidcIdTokenIssuedAt(LocalDateTime oidcIdTokenIssuedAt) { this.oidcIdTokenIssuedAt = oidcIdTokenIssuedAt; }

    public LocalDateTime getOidcIdTokenExpiresAt() { return oidcIdTokenExpiresAt; }
    public void setOidcIdTokenExpiresAt(LocalDateTime oidcIdTokenExpiresAt) { this.oidcIdTokenExpiresAt = oidcIdTokenExpiresAt; }

    public String getOidcIdTokenMetadata() { return oidcIdTokenMetadata; }
    public void setOidcIdTokenMetadata(String oidcIdTokenMetadata) { this.oidcIdTokenMetadata = oidcIdTokenMetadata; }

    public String getOidcIdTokenClaims() { return oidcIdTokenClaims; }
    public void setOidcIdTokenClaims(String oidcIdTokenClaims) { this.oidcIdTokenClaims = oidcIdTokenClaims; }

    // Refresh Token getters/setters
    public String getRefreshTokenValue() { return refreshTokenValue; }
    public void setRefreshTokenValue(String refreshTokenValue) { this.refreshTokenValue = refreshTokenValue; }

    public LocalDateTime getRefreshTokenIssuedAt() { return refreshTokenIssuedAt; }
    public void setRefreshTokenIssuedAt(LocalDateTime refreshTokenIssuedAt) { this.refreshTokenIssuedAt = refreshTokenIssuedAt; }

    public LocalDateTime getRefreshTokenExpiresAt() { return refreshTokenExpiresAt; }
    public void setRefreshTokenExpiresAt(LocalDateTime refreshTokenExpiresAt) { this.refreshTokenExpiresAt = refreshTokenExpiresAt; }

    public String getRefreshTokenMetadata() { return refreshTokenMetadata; }
    public void setRefreshTokenMetadata(String refreshTokenMetadata) { this.refreshTokenMetadata = refreshTokenMetadata; }

    // User Code getters/setters
    public String getUserCodeValue() { return userCodeValue; }
    public void setUserCodeValue(String userCodeValue) { this.userCodeValue = userCodeValue; }

    public LocalDateTime getUserCodeIssuedAt() { return userCodeIssuedAt; }
    public void setUserCodeIssuedAt(LocalDateTime userCodeIssuedAt) { this.userCodeIssuedAt = userCodeIssuedAt; }

    public LocalDateTime getUserCodeExpiresAt() { return userCodeExpiresAt; }
    public void setUserCodeExpiresAt(LocalDateTime userCodeExpiresAt) { this.userCodeExpiresAt = userCodeExpiresAt; }

    public String getUserCodeMetadata() { return userCodeMetadata; }
    public void setUserCodeMetadata(String userCodeMetadata) { this.userCodeMetadata = userCodeMetadata; }

    // Device Code getters/setters
    public String getDeviceCodeValue() { return deviceCodeValue; }
    public void setDeviceCodeValue(String deviceCodeValue) { this.deviceCodeValue = deviceCodeValue; }

    public LocalDateTime getDeviceCodeIssuedAt() { return deviceCodeIssuedAt; }
    public void setDeviceCodeIssuedAt(LocalDateTime deviceCodeIssuedAt) { this.deviceCodeIssuedAt = deviceCodeIssuedAt; }

    public LocalDateTime getDeviceCodeExpiresAt() { return deviceCodeExpiresAt; }
    public void setDeviceCodeExpiresAt(LocalDateTime deviceCodeExpiresAt) { this.deviceCodeExpiresAt = deviceCodeExpiresAt; }

    public String getDeviceCodeMetadata() { return deviceCodeMetadata; }
    public void setDeviceCodeMetadata(String deviceCodeMetadata) { this.deviceCodeMetadata = deviceCodeMetadata; }

    @Override
    public String toString() {
        return "OAuth2Authorization{" +
                "id='" + id + '\'' +
                ", registeredClientId='" + registeredClientId + '\'' +
                ", principalName='" + principalName + '\'' +
                ", authorizationGrantType='" + authorizationGrantType + '\'' +
                '}';
    }
}