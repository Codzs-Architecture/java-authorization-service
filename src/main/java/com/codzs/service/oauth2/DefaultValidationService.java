package com.codzs.service.oauth2;

import com.codzs.exception.type.validation.ValidationException;
import com.codzs.validation.oauth2.OAuth2ParameterValidator;
import com.codzs.validation.oauth2.ParameterValidator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of {@link ValidationService}.
 * This service handles validation operations including parameter validation,
 * OAuth2 validation, and device validation.
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
@Service
public class DefaultValidationService implements ValidationService {

    private final Log logger = LogFactory.getLog(getClass());
    
    private final OAuth2ParameterValidator oauth2ParameterValidator;
    private final ParameterValidator parameterValidator;

    public DefaultValidationService() {
        this.oauth2ParameterValidator = new OAuth2ParameterValidator();
        this.parameterValidator = new ParameterValidator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateAuthorizationRequest(Map<String, String> parameters) throws ValidationException {
        if (logger.isDebugEnabled()) {
            logger.debug("Validating OAuth2 authorization request parameters");
        }

        List<ValidationException.ValidationError> errors = new ArrayList<>();

        // Validate required parameters
        validateRequiredParameterInList(OAuth2ParameterNames.CLIENT_ID, parameters.get(OAuth2ParameterNames.CLIENT_ID), errors);
        validateRequiredParameterInList(OAuth2ParameterNames.RESPONSE_TYPE, parameters.get(OAuth2ParameterNames.RESPONSE_TYPE), errors);
        
        // Validate optional but important parameters
        String redirectUri = parameters.get(OAuth2ParameterNames.REDIRECT_URI);
        if (StringUtils.hasText(redirectUri)) {
            try {
                validateRedirectUri(redirectUri);
            } catch (ValidationException e) {
                errors.addAll(e.getValidationErrors());
            }
        }

        String scope = parameters.get(OAuth2ParameterNames.SCOPE);
        if (StringUtils.hasText(scope)) {
            try {
                validateScope(scope);
            } catch (ValidationException e) {
                errors.addAll(e.getValidationErrors());
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("OAuth2 authorization request validation failed", errors);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateTokenRequest(Map<String, String> parameters) throws ValidationException {
        if (logger.isDebugEnabled()) {
            logger.debug("Validating OAuth2 token request parameters");
        }

        List<ValidationException.ValidationError> errors = new ArrayList<>();

        // Validate required parameters
        validateRequiredParameterInList(OAuth2ParameterNames.GRANT_TYPE, parameters.get(OAuth2ParameterNames.GRANT_TYPE), errors);
        validateRequiredParameterInList(OAuth2ParameterNames.CLIENT_ID, parameters.get(OAuth2ParameterNames.CLIENT_ID), errors);

        if (!errors.isEmpty()) {
            throw new ValidationException("OAuth2 token request validation failed", errors);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateDeviceAuthorizationRequest(Map<String, String> parameters) throws ValidationException {
        if (logger.isDebugEnabled()) {
            logger.debug("Validating device authorization request parameters");
        }

        List<ValidationException.ValidationError> errors = new ArrayList<>();

        // Validate required parameters for device flow
        validateRequiredParameterInList(OAuth2ParameterNames.CLIENT_ID, parameters.get(OAuth2ParameterNames.CLIENT_ID), errors);

        String scope = parameters.get(OAuth2ParameterNames.SCOPE);
        if (StringUtils.hasText(scope)) {
            try {
                validateScope(scope);
            } catch (ValidationException e) {
                errors.addAll(e.getValidationErrors());
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Device authorization request validation failed", errors);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateDeviceTokenRequest(Map<String, String> parameters) throws ValidationException {
        if (logger.isDebugEnabled()) {
            logger.debug("Validating device token request parameters");
        }

        List<ValidationException.ValidationError> errors = new ArrayList<>();

        // Validate required parameters for device token request
        validateRequiredParameterInList(OAuth2ParameterNames.GRANT_TYPE, parameters.get(OAuth2ParameterNames.GRANT_TYPE), errors);
        validateRequiredParameterInList(OAuth2ParameterNames.CLIENT_ID, parameters.get(OAuth2ParameterNames.CLIENT_ID), errors);
        validateRequiredParameterInList(OAuth2ParameterNames.DEVICE_CODE, parameters.get(OAuth2ParameterNames.DEVICE_CODE), errors);

        if (!errors.isEmpty()) {
            throw new ValidationException("Device token request validation failed", errors);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateClientId(String clientId) throws ValidationException {
        oauth2ParameterValidator.validateClientId(clientId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateScope(String scope) throws ValidationException {
        oauth2ParameterValidator.validateScope(scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateRedirectUri(String redirectUri) throws ValidationException {
        oauth2ParameterValidator.validateRedirectUri(redirectUri);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateUserCode(String userCode) throws ValidationException {
        oauth2ParameterValidator.validateUserCode(userCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateDeviceCode(String deviceCode) throws ValidationException {
        oauth2ParameterValidator.validateDeviceCode(deviceCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateRequiredParameter(String parameterName, String value) throws ValidationException {
        parameterValidator.validateRequired(parameterName, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateParameterPattern(String parameterName, String value, String pattern, String patternDescription) throws ValidationException {
        parameterValidator.validatePattern(parameterName, value, pattern, patternDescription);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateParameterLength(String parameterName, String value, int minLength, int maxLength) throws ValidationException {
        parameterValidator.validateLength(parameterName, value, minLength, maxLength);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValidParameter(String parameterName, String value) {
        try {
            validateRequiredParameter(parameterName, value);
            return true;
        } catch (ValidationException e) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationResult validateParameter(String parameterName, String value) {
        try {
            validateRequiredParameter(parameterName, value);
            return ValidationResult.success(parameterName);
        } catch (ValidationException e) {
            String errorMessage = e.getValidationErrors().isEmpty() ? 
                e.getMessage() : e.getValidationErrors().get(0).getMessage();
            return ValidationResult.failure(parameterName, errorMessage);
        }
    }

    /**
     * Helper method to validate required parameters and collect errors.
     */
    private void validateRequiredParameterInList(String parameterName, String value, List<ValidationException.ValidationError> errors) {
        try {
            validateRequiredParameter(parameterName, value);
        } catch (ValidationException e) {
            errors.addAll(e.getValidationErrors());
        }
    }
} 