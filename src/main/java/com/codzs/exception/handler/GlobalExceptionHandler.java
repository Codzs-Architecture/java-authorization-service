package com.codzs.exception.handler;

import com.codzs.exception.AuthorizationServiceException;
import com.codzs.exception.authentication.AuthenticationException;
import com.codzs.exception.device.DeviceFlowException;
import com.codzs.exception.oauth2.InvalidClientException;
import com.codzs.exception.oauth2.OAuth2Exception;
import com.codzs.exception.validation.ValidationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
// import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

// import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Global exception handler for the OAuth2 Authorization Service.
 * This class handles all exceptions thrown by the application and provides
 * consistent error responses with appropriate HTTP status codes.
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private final Log logger = LogFactory.getLog(getClass());

    /**
     * Handles OAuth2-specific exceptions.
     * 
     * @param ex the OAuth2 exception
     * @param request the web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(OAuth2Exception.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleOAuth2Exception(OAuth2Exception ex, WebRequest request) {
        String errorId = generateErrorId();
        
        if (logger.isDebugEnabled()) {
            logger.debug("OAuth2 exception occurred [" + errorId + "]", ex);
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
            .errorId(errorId)
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("OAuth2 Error")
            .message(ex.getMessage())
            .path(getPath(request))
            .oauth2Error(OAuth2ErrorDetails.builder()
                .error(ex.getOAuth2ErrorCode())
                .errorDescription(ex.getOAuth2ErrorDescription())
                .errorUri(ex.getOAuth2ErrorUri())
                .build())
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles invalid client exceptions.
     * 
     * @param ex the invalid client exception
     * @param request the web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(InvalidClientException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleInvalidClientException(InvalidClientException ex, WebRequest request) {
        String errorId = generateErrorId();
        
        if (logger.isWarnEnabled()) {
            logger.warn("Invalid client exception occurred [" + errorId + "]: " + ex.getMessage());
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
            .errorId(errorId)
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.UNAUTHORIZED.value())
            .error("Invalid Client")
            .message(ex.getMessage())
            .path(getPath(request))
            .oauth2Error(OAuth2ErrorDetails.builder()
                .error(ex.getOAuth2ErrorCode())
                .errorDescription(ex.getOAuth2ErrorDescription())
                .errorUri(ex.getOAuth2ErrorUri())
                .build())
            .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * Handles device flow exceptions.
     * 
     * @param ex the device flow exception
     * @param request the web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(DeviceFlowException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleDeviceFlowException(DeviceFlowException ex, WebRequest request) {
        String errorId = generateErrorId();
        
        if (logger.isDebugEnabled()) {
            logger.debug("Device flow exception occurred [" + errorId + "]", ex);
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
            .errorId(errorId)
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Device Flow Error")
            .message(ex.getMessage())
            .path(getPath(request))
            .oauth2Error(OAuth2ErrorDetails.builder()
                .error(ex.getOAuth2ErrorCode())
                .errorDescription(ex.getOAuth2ErrorDescription())
                .errorUri(ex.getOAuth2ErrorUri())
                .build())
            .deviceContext(DeviceContextDetails.builder()
                .deviceCode(ex.getDeviceCode())
                .userCode(ex.getUserCode())
                .build())
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles validation exceptions.
     * 
     * @param ex the validation exception
     * @param request the web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex, WebRequest request) {
        String errorId = generateErrorId();
        
        if (logger.isDebugEnabled()) {
            logger.debug("Validation exception occurred [" + errorId + "]", ex);
        }

        List<FieldError> fieldErrors = new ArrayList<>();
        for (ValidationException.ValidationError validationError : ex.getValidationErrors()) {
            fieldErrors.add(FieldError.builder()
                .field(validationError.getField())
                .message(validationError.getMessage())
                .rejectedValue(validationError.getRejectedValue())
                .build());
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
            .errorId(errorId)
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation Error")
            .message(ex.getMessage())
            .path(getPath(request))
            .fieldErrors(fieldErrors)
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles custom authentication exceptions.
     * 
     * @param ex the authentication exception
     * @param request the web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        String errorId = generateErrorId();
        
        if (logger.isWarnEnabled()) {
            logger.warn("Authentication exception occurred [" + errorId + "]: " + ex.getMessage());
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
            .errorId(errorId)
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.UNAUTHORIZED.value())
            .error("Authentication Error")
            .message(ex.getMessage())
            .path(getPath(request))
            .authenticationContext(AuthenticationContextDetails.builder()
                .username(ex.getUsername())
                .authenticationType(ex.getAuthenticationType())
                .realm(ex.getRealm())
                .build())
            .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * Handles Spring Security authentication exceptions.
     * 
     * @param ex the Spring authentication exception
     * @param request the web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleSpringAuthenticationException(org.springframework.security.core.AuthenticationException ex, WebRequest request) {
        String errorId = generateErrorId();
        
        if (logger.isWarnEnabled()) {
            logger.warn("Spring authentication exception occurred [" + errorId + "]: " + ex.getMessage());
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
            .errorId(errorId)
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.UNAUTHORIZED.value())
            .error("Authentication Required")
            .message("Authentication is required to access this resource")
            .path(getPath(request))
            .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * Handles access denied exceptions.
     * 
     * @param ex the access denied exception
     * @param request the web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        String errorId = generateErrorId();
        
        if (logger.isWarnEnabled()) {
            logger.warn("Access denied exception occurred [" + errorId + "]: " + ex.getMessage());
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
            .errorId(errorId)
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.FORBIDDEN.value())
            .error("Access Denied")
            .message("Access is denied")
            .path(getPath(request))
            .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    /**
     * Handles NoHandlerFoundException (404 errors).
     * 
     * @param ex the no handler found exception
     * @param request the web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException ex, WebRequest request) {
        String errorId = generateErrorId();
        
        if (logger.isDebugEnabled()) {
            logger.debug("No handler found exception occurred [" + errorId + "]: " + ex.getMessage());
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
            .errorId(errorId)
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Not Found")
            .message("The requested resource was not found")
            .path(getPath(request))
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handles general authorization service exceptions.
     * 
     * @param ex the authorization service exception
     * @param request the web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(AuthorizationServiceException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleAuthorizationServiceException(AuthorizationServiceException ex, WebRequest request) {
        String errorId = generateErrorId();
        
        if (logger.isErrorEnabled()) {
            logger.error("Authorization service exception occurred [" + errorId + "]", ex);
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
            .errorId(errorId)
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("Authorization Service Error")
            .message(ex.getMessage())
            .path(getPath(request))
            .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Handles all other exceptions.
     * 
     * @param ex the exception
     * @param request the web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        String errorId = generateErrorId();
        
        if (logger.isErrorEnabled()) {
            logger.error("Unexpected exception occurred [" + errorId + "]", ex);
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
            .errorId(errorId)
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("Internal Server Error")
            .message("An unexpected error occurred")
            .path(getPath(request))
            .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Generates a unique error ID for tracking purposes.
     * 
     * @return a unique error ID
     */
    private String generateErrorId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Extracts the request path from the web request.
     * 
     * @param request the web request
     * @return the request path
     */
    private String getPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
} 