package com.codzs.validation.oauth2;

import com.codzs.exception.validation.ValidationException;
import com.codzs.constant.OAuth2Constant;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Validator for OAuth2-specific parameters.
 * This class provides validation logic for OAuth2 parameters including
 * client IDs, scopes, redirect URIs, and device flow codes.
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
public class OAuth2ParameterValidator {

    // OAuth2 parameter validation patterns
    private static final Pattern CLIENT_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{3,128}$");
    private static final Pattern SCOPE_PATTERN = Pattern.compile("^[a-zA-Z0-9_\\-\\.:]+(?:\\s+[a-zA-Z0-9_\\-\\.:]+)*$");
    private static final Pattern USER_CODE_PATTERN = Pattern.compile("^[A-Z0-9]{4}-[A-Z0-9]{4}$");
    private static final Pattern DEVICE_CODE_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{20,128}$");

    // Constants for validation
    private static final int CLIENT_ID_MIN_LENGTH = 3;
    private static final int CLIENT_ID_MAX_LENGTH = 128;
    private static final int SCOPE_MAX_LENGTH = 1000;
    private static final int USER_CODE_LENGTH = 9; // "XXXX-XXXX"
    private static final int DEVICE_CODE_MIN_LENGTH = 20;
    private static final int DEVICE_CODE_MAX_LENGTH = 128;

    /**
     * Validates OAuth2 client ID.
     * 
     * @param clientId the client ID to validate
     * @throws ValidationException if validation fails
     */
    public void validateClientId(String clientId) throws ValidationException {
        if (!StringUtils.hasText(clientId)) {
            throw ValidationException.requiredField(OAuth2Constant.Parameters.CLIENT_ID);
        }

        if (clientId.length() < CLIENT_ID_MIN_LENGTH || clientId.length() > CLIENT_ID_MAX_LENGTH) {
            throw ValidationException.valueOutOfRange(OAuth2Constant.Parameters.CLIENT_ID, CLIENT_ID_MIN_LENGTH, CLIENT_ID_MAX_LENGTH);
        }

        if (!CLIENT_ID_PATTERN.matcher(clientId).matches()) {
            throw ValidationException.invalidFormat(OAuth2Constant.Parameters.CLIENT_ID, "alphanumeric characters, hyphens, and underscores only");
        }
    }

    /**
     * Validates OAuth2 scope parameter.
     * 
     * @param scope the scope parameter to validate
     * @throws ValidationException if validation fails
     */
    public void validateScope(String scope) throws ValidationException {
        if (!StringUtils.hasText(scope)) {
            throw ValidationException.requiredField(OAuth2Constant.Parameters.SCOPE);
        }

        if (scope.length() > SCOPE_MAX_LENGTH) {
            throw new ValidationException(OAuth2Constant.ValidationMessages.SCOPE_VALIDATION_FAILED, OAuth2Constant.Parameters.SCOPE, 
                "Scope parameter exceeds maximum length of " + SCOPE_MAX_LENGTH + " characters");
        }

        if (!SCOPE_PATTERN.matcher(scope).matches()) {
            throw ValidationException.invalidFormat(OAuth2Constant.Parameters.SCOPE, "space-separated scope values with alphanumeric characters, hyphens, underscores, colons, and periods");
        }

        // Check for duplicate scopes using Set for better performance
        String[] scopes = scope.split("\\s+");
        Set<String> uniqueScopes = new HashSet<>();
        for (String scopeValue : scopes) {
            if (!uniqueScopes.add(scopeValue)) {
                throw new ValidationException(OAuth2Constant.ValidationMessages.SCOPE_VALIDATION_FAILED, OAuth2Constant.Parameters.SCOPE, 
                    "Duplicate scope value: " + scopeValue);
            }
        }
    }

    /**
     * Validates OAuth2 redirect URI.
     * 
     * @param redirectUri the redirect URI to validate
     * @throws ValidationException if validation fails
     */
    public void validateRedirectUri(String redirectUri) throws ValidationException {
        if (!StringUtils.hasText(redirectUri)) {
            throw ValidationException.requiredField(OAuth2Constant.Parameters.REDIRECT_URI);
        }

        try {
            URI uri = new URI(redirectUri);
            
            // Check for required scheme
            if (uri.getScheme() == null) {
                throw new ValidationException(OAuth2Constant.ValidationMessages.REDIRECT_URI_VALIDATION_FAILED, OAuth2Constant.Parameters.REDIRECT_URI, 
                    "Redirect URI must have a scheme (http or https)");
            }

            // Check for allowed schemes
            String scheme = uri.getScheme().toLowerCase();
            if (!(OAuth2Constant.Schemes.HTTP.equals(scheme) || OAuth2Constant.Schemes.HTTPS.equals(scheme))) {
                throw new ValidationException(OAuth2Constant.ValidationMessages.REDIRECT_URI_VALIDATION_FAILED, OAuth2Constant.Parameters.REDIRECT_URI, 
                    "Redirect URI scheme must be http or https");
            }

            // Check for fragment
            if (uri.getFragment() != null) {
                throw new ValidationException(OAuth2Constant.ValidationMessages.REDIRECT_URI_VALIDATION_FAILED, OAuth2Constant.Parameters.REDIRECT_URI, 
                    "Redirect URI must not contain a fragment");
            }

            // For production, require HTTPS
            if (OAuth2Constant.Schemes.HTTP.equals(scheme) && !isLocalhost(uri.getHost())) {
                throw new ValidationException(OAuth2Constant.ValidationMessages.REDIRECT_URI_VALIDATION_FAILED, OAuth2Constant.Parameters.REDIRECT_URI, 
                    "Redirect URI must use HTTPS for non-localhost domains");
            }

        } catch (URISyntaxException e) {
            throw new ValidationException(OAuth2Constant.ValidationMessages.REDIRECT_URI_VALIDATION_FAILED, OAuth2Constant.Parameters.REDIRECT_URI, 
                "Invalid URI format: " + e.getMessage());
        }
    }

    /**
     * Validates user code for device flow.
     * 
     * @param userCode the user code to validate
     * @throws ValidationException if validation fails
     */
    public void validateUserCode(String userCode) throws ValidationException {
        if (!StringUtils.hasText(userCode)) {
            throw ValidationException.requiredField("user_code");
        }

        if (userCode.length() != USER_CODE_LENGTH) {
            throw new ValidationException("User code validation failed", "user_code", 
                "User code must be exactly " + USER_CODE_LENGTH + " characters in format XXXX-XXXX");
        }

        if (!USER_CODE_PATTERN.matcher(userCode).matches()) {
            throw ValidationException.invalidFormat("user_code", "XXXX-XXXX where X is an uppercase letter or digit");
        }
    }

    /**
     * Validates device code for device flow.
     * 
     * @param deviceCode the device code to validate
     * @throws ValidationException if validation fails
     */
    public void validateDeviceCode(String deviceCode) throws ValidationException {
        if (!StringUtils.hasText(deviceCode)) {
            throw ValidationException.requiredField("device_code");
        }

        if (deviceCode.length() < DEVICE_CODE_MIN_LENGTH || deviceCode.length() > DEVICE_CODE_MAX_LENGTH) {
            throw ValidationException.valueOutOfRange("device_code", DEVICE_CODE_MIN_LENGTH, DEVICE_CODE_MAX_LENGTH);
        }

        if (!DEVICE_CODE_PATTERN.matcher(deviceCode).matches()) {
            throw ValidationException.invalidFormat("device_code", "alphanumeric characters, hyphens, and underscores only");
        }
    }

    /**
     * Checks if a host is localhost.
     * 
     * @param host the host to check
     * @return true if the host is localhost, false otherwise
     */
    private boolean isLocalhost(String host) {
        return host != null && (
            "localhost".equalsIgnoreCase(host) ||
            "127.0.0.1".equals(host) ||
            "::1".equals(host) ||
            host.startsWith("localhost:") ||
            host.startsWith("127.0.0.1:") ||
            host.startsWith("[::1]:")
        );
    }
} 