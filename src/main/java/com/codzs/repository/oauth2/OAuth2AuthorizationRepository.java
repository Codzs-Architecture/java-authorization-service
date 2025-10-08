package com.codzs.repository.oauth2;

import com.codzs.entity.oauth2.OAuth2Authorization;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for OAuth2Authorization MongoDB documents.
 * Provides methods for managing OAuth2 authorization data including tokens and codes.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Repository
public interface OAuth2AuthorizationRepository extends MongoRepository<OAuth2Authorization, String> {

    /**
     * Find authorization by state.
     * 
     * @param state the state value
     * @return Optional containing the authorization if found
     */
    Optional<OAuth2Authorization> findByState(String state);

    /**
     * Find authorization by authorization code value.
     * 
     * @param authorizationCodeValue the authorization code
     * @return Optional containing the authorization if found
     */
    Optional<OAuth2Authorization> findByAuthorizationCodeValue(String authorizationCodeValue);

    /**
     * Find authorization by access token value.
     * 
     * @param accessTokenValue the access token
     * @return Optional containing the authorization if found
     */
    Optional<OAuth2Authorization> findByAccessTokenValue(String accessTokenValue);

    /**
     * Find authorization by refresh token value.
     * 
     * @param refreshTokenValue the refresh token
     * @return Optional containing the authorization if found
     */
    Optional<OAuth2Authorization> findByRefreshTokenValue(String refreshTokenValue);

    /**
     * Find authorization by OIDC ID token value.
     * 
     * @param oidcIdTokenValue the OIDC ID token
     * @return Optional containing the authorization if found
     */
    Optional<OAuth2Authorization> findByOidcIdTokenValue(String oidcIdTokenValue);

    /**
     * Find authorization by user code value.
     * 
     * @param userCodeValue the user code
     * @return Optional containing the authorization if found
     */
    Optional<OAuth2Authorization> findByUserCodeValue(String userCodeValue);

    /**
     * Find authorization by device code value.
     * 
     * @param deviceCodeValue the device code
     * @return Optional containing the authorization if found
     */
    Optional<OAuth2Authorization> findByDeviceCodeValue(String deviceCodeValue);

    /**
     * Find all authorizations for a specific registered client.
     * 
     * @param registeredClientId the registered client ID
     * @return list of authorizations for the client
     */
    List<OAuth2Authorization> findByRegisteredClientId(String registeredClientId);

    /**
     * Find all authorizations for a specific principal.
     * 
     * @param principalName the principal name
     * @return list of authorizations for the principal
     */
    List<OAuth2Authorization> findByPrincipalName(String principalName);

    /**
     * Find authorizations by grant type.
     * 
     * @param authorizationGrantType the grant type
     * @return list of authorizations with the grant type
     */
    List<OAuth2Authorization> findByAuthorizationGrantType(String authorizationGrantType);

    /**
     * Find expired access tokens for cleanup.
     * 
     * @param now current timestamp
     * @return list of authorizations with expired access tokens
     */
    @Query("{ 'accessTokenExpiresAt': { $ne: null, $lte: ?0 } }")
    List<OAuth2Authorization> findExpiredAccessTokens(LocalDateTime now);

    /**
     * Find expired refresh tokens for cleanup.
     * 
     * @param now current timestamp
     * @return list of authorizations with expired refresh tokens
     */
    @Query("{ 'refreshTokenExpiresAt': { $ne: null, $lte: ?0 } }")
    List<OAuth2Authorization> findExpiredRefreshTokens(LocalDateTime now);

    /**
     * Find expired authorization codes for cleanup.
     * 
     * @param now current timestamp
     * @return list of authorizations with expired codes
     */
    @Query("{ 'authorizationCodeExpiresAt': { $ne: null, $lte: ?0 } }")
    List<OAuth2Authorization> findExpiredAuthorizationCodes(LocalDateTime now);

    /**
     * Find expired OIDC ID tokens for cleanup.
     * 
     * @param now current timestamp
     * @return list of authorizations with expired OIDC ID tokens
     */
    @Query("{ 'oidcIdTokenExpiresAt': { $ne: null, $lte: ?0 } }")
    List<OAuth2Authorization> findExpiredOidcIdTokens(LocalDateTime now);

    /**
     * Find expired user codes for cleanup.
     * 
     * @param now current timestamp
     * @return list of authorizations with expired user codes
     */
    @Query("{ 'userCodeExpiresAt': { $ne: null, $lte: ?0 } }")
    List<OAuth2Authorization> findExpiredUserCodes(LocalDateTime now);

    /**
     * Find expired device codes for cleanup.
     * 
     * @param now current timestamp
     * @return list of authorizations with expired device codes
     */
    @Query("{ 'deviceCodeExpiresAt': { $ne: null, $lte: ?0 } }")
    List<OAuth2Authorization> findExpiredDeviceCodes(LocalDateTime now);

    /**
     * Delete authorizations for a specific registered client.
     * 
     * @param registeredClientId the registered client ID
     * @return number of deleted records
     */
    long deleteByRegisteredClientId(String registeredClientId);

    /**
     * Delete authorizations for a specific principal.
     * 
     * @param principalName the principal name
     * @return number of deleted records
     */
    long deleteByPrincipalName(String principalName);

    /**
     * Delete authorizations with expired tokens and codes.
     * 
     * @param now current timestamp
     * @return number of deleted records
     */
    @Query(value = "{ $or: [ " +
           "{ 'accessTokenExpiresAt': { $ne: null, $lte: ?0 } }, " +
           "{ 'refreshTokenExpiresAt': { $ne: null, $lte: ?0 } }, " +
           "{ 'authorizationCodeExpiresAt': { $ne: null, $lte: ?0 } }, " +
           "{ 'oidcIdTokenExpiresAt': { $ne: null, $lte: ?0 } }, " +
           "{ 'userCodeExpiresAt': { $ne: null, $lte: ?0 } }, " +
           "{ 'deviceCodeExpiresAt': { $ne: null, $lte: ?0 } } ] }", 
           delete = true)
    long deleteExpiredAuthorizations(LocalDateTime now);
}