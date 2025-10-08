package com.codzs.entity.oauth2;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * MongoDB Document representing OAuth2 authorization consent.
 * This entity stores user consent for OAuth2 client access.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Document(collection = "oauth2_authorization_consent")
@CompoundIndexes({
    @CompoundIndex(name = "idx_oauth2_consent_client_principal", def = "{'registeredClientId': 1, 'principalName': 1}", unique = true)
})
public class OAuth2AuthorizationConsent {

    @Id
    private String id;

    @NotBlank(message = "Registered client ID is required")
    @Size(max = 100, message = "Registered client ID must not exceed 100 characters")
    private String registeredClientId;

    @NotBlank(message = "Principal name is required")
    @Size(max = 200, message = "Principal name must not exceed 200 characters")
    private String principalName;

    @NotBlank(message = "Authorities are required")
    @Size(max = 1000, message = "Authorities must not exceed 1000 characters")
    private String authorities;

    // Constructors
    public OAuth2AuthorizationConsent() {}

    public OAuth2AuthorizationConsent(String registeredClientId, String principalName, String authorities) {
        this.registeredClientId = registeredClientId;
        this.principalName = principalName;
        this.authorities = authorities;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRegisteredClientId() { return registeredClientId; }
    public void setRegisteredClientId(String registeredClientId) { this.registeredClientId = registeredClientId; }

    public String getPrincipalName() { return principalName; }
    public void setPrincipalName(String principalName) { this.principalName = principalName; }

    public String getAuthorities() { return authorities; }
    public void setAuthorities(String authorities) { this.authorities = authorities; }

    @Override
    public String toString() {
        return "OAuth2AuthorizationConsent{" +
                "id='" + id + '\'' +
                ", registeredClientId='" + registeredClientId + '\'' +
                ", principalName='" + principalName + '\'' +
                ", authorities='" + authorities + '\'' +
                '}';
    }
}