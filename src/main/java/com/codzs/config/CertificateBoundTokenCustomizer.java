package com.codzs.config;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.codzs.constants.OAuth2Constants;
import com.codzs.oauth2.authentication.federation.token.ClaimExtractor;

/**
 * Combined JWT token customizer that handles both:
 * 1. Certificate confirmation (cnf) claims for certificate-bound access tokens as per RFC 8705
 * 2. Federated identity claims for ID tokens
 */
@Component
@Primary
public class CertificateBoundTokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    private final Log logger = LogFactory.getLog(getClass());

    @Override
    public void customize(JwtEncodingContext context) {
        // Handle ID token customization for federated identity
        if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
            Map<String, Object> thirdPartyClaims = ClaimExtractor.extractClaims(context.getPrincipal());
            context.getClaims().claims(existingClaims -> {
                // Remove conflicting claims set by this authorization server
                existingClaims.keySet().forEach(thirdPartyClaims::remove);
                // Remove standard id_token claims that could cause problems with clients
                ClaimExtractor.STANDARD_ID_TOKEN_CLAIMS.forEach(thirdPartyClaims::remove);
                // Add all other claims directly to id_token
                existingClaims.putAll(thirdPartyClaims);
            });
            return;
        }

        // Handle access token customization for certificate-bound tokens
        if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
            RegisteredClient registeredClient = context.getRegisteredClient();
            
            // Check if certificate-bound tokens are enabled for this client
            Boolean certificateBoundTokens = registeredClient.getTokenSettings()
                .getSetting(OAuth2Constants.TokenSettings.X509_CERTIFICATE_BOUND_ACCESS_TOKENS);
                
            if (Boolean.TRUE.equals(certificateBoundTokens)) {
                // Extract certificate from authentication context
                X509Certificate certificate = extractCertificate(context);
                if (certificate != null) {
                    String thumbprint = calculateThumbprint(certificate);
                    if (thumbprint != null) {
                        // Add cnf claim with certificate thumbprint
                        context.getClaims().claim(OAuth2Constants.Claims.CNF, 
                            Map.of(OAuth2Constants.Claims.X5T_S256, thumbprint));
                    }
                }
            }
        }
    }

    private X509Certificate extractCertificate(JwtEncodingContext context) {
        try {
            // Extract certificate from OAuth2 client authentication
            if (context.getPrincipal() instanceof OAuth2ClientAuthenticationToken authToken) {
                Object credentials = authToken.getCredentials();
                if (credentials instanceof X509Certificate) {
                    return (X509Certificate) credentials;
                }
                // Handle case where credentials might be an array
                if (credentials instanceof X509Certificate[]) {
                    X509Certificate[] certs = (X509Certificate[]) credentials;
                    return certs.length > 0 ? certs[0] : null;
                }
            }
        } catch (Exception e) {
            // Log error but don't fail token generation
            if (logger.isWarnEnabled()) {
                logger.warn("Failed to extract certificate for certificate-bound token", e);
            }
        }
        return null;
    }

    private String calculateThumbprint(X509Certificate certificate) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] certBytes = certificate.getEncoded();
            byte[] thumbprintBytes = digest.digest(certBytes);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(thumbprintBytes);
        } catch (NoSuchAlgorithmException | CertificateEncodingException e) {
            if (logger.isWarnEnabled()) {
                logger.warn("Failed to calculate certificate thumbprint", e);
            }
            return null;
        }
    }
}