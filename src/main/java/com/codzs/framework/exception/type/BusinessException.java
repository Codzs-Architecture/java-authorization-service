package com.codzs.framework.exception.type;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base exception for business logic violations.
 * Provides standardized business error handling with proper HTTP status mapping.
 * 
 * @author CodeGeneration Framework
 * @since 1.0
 */
@Getter
public class BusinessException extends RuntimeException {
    
    private final String errorCode;
    private final HttpStatus httpStatus;
    private final Object[] messageArgs;
    
    public BusinessException(String errorCode, String message) {
        this(errorCode, message, HttpStatus.BAD_REQUEST);
    }
    
    public BusinessException(String errorCode, String message, HttpStatus httpStatus) {
        this(errorCode, message, httpStatus, (Object[]) null);
    }
    
    public BusinessException(String errorCode, String message, Object... messageArgs) {
        this(errorCode, message, HttpStatus.BAD_REQUEST, messageArgs);
    }
    
    public BusinessException(String errorCode, String message, HttpStatus httpStatus, Object... messageArgs) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.messageArgs = messageArgs;
    }
    
    public BusinessException(String errorCode, String message, Throwable cause) {
        this(errorCode, message, HttpStatus.BAD_REQUEST, cause);
    }
    
    public BusinessException(String errorCode, String message, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.messageArgs = null;
    }
}