package com.codzs.exception.handler;

import com.codzs.framework.exception.handler.BaseExceptionHandler;
import com.codzs.framework.exception.type.ServiceException;
import com.codzs.framework.exception.bean.StandardErrorResponse;
import com.codzs.framework.exception.context.RequestContext;
import com.codzs.exception.type.oauth2.OAuth2Exception;
import com.codzs.exception.type.oauth2.InvalidClientException;
import com.codzs.exception.type.oauth2.AuthenticationException;
import com.codzs.exception.constant.AuthorizationErrorCodes;
import com.codzs.exception.type.device.DeviceFlowException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

/**
 * Authorization service specific exception handler that handles OAuth2,
 * authentication, and authorization-related exceptions.
 * 
 * This handler is processed before the global framework exception handler
 * to provide specific handling for authorization service exceptions.
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
@Slf4j
@ControllerAdvice
@Order(1)
public class AuthorizationExceptionHandler extends BaseExceptionHandler {

    // ===== OAUTH2 EXCEPTION HANDLERS =====

    /**
     * Handles OAuth2-specific exceptions with standardized response format.
     */
    @ExceptionHandler(OAuth2Exception.class)
    public ResponseEntity<StandardErrorResponse> handleOAuth2Exception(
            OAuth2Exception ex, HttpServletRequest request) {
        
        String errorId = generateErrorId();
        RequestContext context = extractMultiTenantContext(request);
        
        log.warn("OAuth2 exception [{}]: {} - Organization: {}, Tenant: {}", 
                errorId, ex.getMessage(), context.getOrganizationId(), context.getTenantId());
        
        StandardErrorResponse response = StandardErrorResponse.builder()
                .errorId(errorId)
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .errorCode(ex.getErrorCode())
                .path(request.getRequestURI())
                .method(request.getMethod())
                .requestContext(context)
                .oauthError(ex.getOAuth2ErrorCode())
                .oauthErrorDescription(ex.getOAuth2ErrorDescription())
                .oauthErrorUri(ex.getOAuth2ErrorUri())
                .correlationId(generateErrorId())
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handles OAuth2 invalid client exceptions.
     */
    @ExceptionHandler(InvalidClientException.class)
    public ResponseEntity<StandardErrorResponse> handleInvalidClientException(
            InvalidClientException ex, HttpServletRequest request) {
        
        String errorId = generateErrorId();
        RequestContext context = extractMultiTenantContext(request);
        
        log.warn("Invalid client exception [{}]: {} - Organization: {}, Tenant: {}", 
                errorId, ex.getMessage(), context.getOrganizationId(), context.getTenantId());
        
        StandardErrorResponse response = StandardErrorResponse.builder()
                .errorId(errorId)
                .timestamp(Instant.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(ex.getMessage())
                .errorCode(AuthorizationErrorCodes.INVALID_CLIENT)
                .path(request.getRequestURI())
                .method(request.getMethod())
                .requestContext(context)
                .oauthError(AuthorizationErrorCodes.INVALID_CLIENT)
                .oauthErrorDescription(ex.getMessage())
                .correlationId(generateErrorId())
                .build();
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Handles device flow exceptions with device-specific fields.
     */
    @ExceptionHandler(DeviceFlowException.class)
    public ResponseEntity<StandardErrorResponse> handleDeviceFlowException(
            DeviceFlowException ex, HttpServletRequest request) {
        
        String errorId = generateErrorId();
        RequestContext context = extractMultiTenantContext(request);
        
        log.warn("Device flow exception [{}]: {} - Organization: {}, Tenant: {}", 
                errorId, ex.getMessage(), context.getOrganizationId(), context.getTenantId());
        
        StandardErrorResponse response = StandardErrorResponse.builder()
                .errorId(errorId)
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .errorCode(ex.getErrorCode())
                .path(request.getRequestURI())
                .method(request.getMethod())
                .requestContext(context)
                .oauthError(ex.getOAuth2ErrorCode())
                .oauthErrorDescription(ex.getOAuth2ErrorDescription())
                .deviceCode(ex.getDeviceCode())
                .userCode(ex.getUserCode())
                .correlationId(generateErrorId())
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handles custom authentication exceptions.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<StandardErrorResponse> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {
        
        String errorId = generateErrorId();
        RequestContext context = extractMultiTenantContext(request);
        
        log.warn("Authentication exception [{}]: {} - Organization: {}, Tenant: {}", 
                errorId, ex.getMessage(), context.getOrganizationId(), context.getTenantId());
        
        StandardErrorResponse response = StandardErrorResponse.builder()
                .errorId(errorId)
                .timestamp(Instant.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(ex.getMessage())
                .errorCode(AuthorizationErrorCodes.AUTHENTICATION_ERROR)
                .path(request.getRequestURI())
                .method(request.getMethod())
                .requestContext(context)
                .oauthError(AuthorizationErrorCodes.ACCESS_DENIED)
                .oauthErrorDescription("Authentication failed")
                .correlationId(generateErrorId())
                .build();
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Handles authorization service exceptions.
     */
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<StandardErrorResponse> handleAuthorizationServiceException(
            ServiceException ex, HttpServletRequest request) {
        
        String errorId = generateErrorId();
        RequestContext context = extractMultiTenantContext(request);
        
        log.error("Authorization service exception [{}]: {} - Organization: {}, Tenant: {}", 
                errorId, ex.getMessage(), context.getOrganizationId(), context.getTenantId(), ex);
        
        StandardErrorResponse response = StandardErrorResponse.builder()
                .errorId(errorId)
                .timestamp(Instant.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(ex.getMessage())
                .errorCode(ex.getErrorCode())
                .path(request.getRequestURI())
                .method(request.getMethod())
                .requestContext(context)
                .oauthError(AuthorizationErrorCodes.SERVER_ERROR)
                .oauthErrorDescription("Authorization service error occurred")
                .correlationId(generateErrorId())
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // Note: Common helper methods are now inherited from BaseExceptionHandler
}