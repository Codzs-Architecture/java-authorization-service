package com.codzs.exception.factory;
// package com.codzs.exception.handler;

// import com.codzs.context.oauth2.AuthenticationContextDetails;
// import com.codzs.context.oauth2.DeviceContextDetails;
// import com.codzs.exception.validation.ValidationException;
// import org.springframework.http.HttpStatus;
// import org.springframework.security.oauth2.core.OAuth2ErrorCodes;

// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.UUID;

// /**
//  * Factory class for creating standardized error responses.
//  * This class provides utility methods for creating consistent error responses
//  * with appropriate HTTP status codes and error details.
//  * 
//  * @author Nitin Khaitan
//  * @since 1.1
//  */
// public final class ErrorResponseFactory {
    
//     // Private constructor to prevent instantiation
//     private ErrorResponseFactory() {}
    
//     /**
//      * Creates a generic error response with minimal information.
//      * 
//      * @param status HTTP status code
//      * @param error error type
//      * @param message error message
//      * @param path request path
//      * @return standardized error response
//      */
//     public static ErrorResponse createGenericError(HttpStatus status, String error, String message, String path) {
//         return ErrorResponse.builder()
//             .errorId(generateErrorId())
//             .timestamp(LocalDateTime.now())
//             .status(status.value())
//             .error(error)
//             .message(message)
//             .path(path)
//             .build();
//     }
    
//     /**
//      * Creates an OAuth2 error response.
//      * 
//      * @param status HTTP status code
//      * @param oauth2ErrorCode OAuth2 error code
//      * @param errorDescription error description
//      * @param path request path
//      * @return OAuth2 error response
//      */
//     public static ErrorResponse createOAuth2Error(HttpStatus status, String oauth2ErrorCode, String errorDescription, String path) {
//         return ErrorResponse.builder()
//             .errorId(generateErrorId())
//             .timestamp(LocalDateTime.now())
//             .status(status.value())
//             .error("OAuth2 Error")
//             .message(errorDescription)
//             .path(path)
//             .oauth2Error(OAuth2ErrorDetails.builder()
//                 .error(oauth2ErrorCode)
//                 .errorDescription(errorDescription)
//                 .build())
//             .build();
//     }
    
//     /**
//      * Creates an OAuth2 error response with URI.
//      * 
//      * @param status HTTP status code
//      * @param oauth2ErrorCode OAuth2 error code
//      * @param errorDescription error description
//      * @param errorUri error URI
//      * @param path request path
//      * @return OAuth2 error response
//      */
//     public static ErrorResponse createOAuth2Error(HttpStatus status, String oauth2ErrorCode, String errorDescription, String errorUri, String path) {
//         return ErrorResponse.builder()
//             .errorId(generateErrorId())
//             .timestamp(LocalDateTime.now())
//             .status(status.value())
//             .error("OAuth2 Error")
//             .message(errorDescription)
//             .path(path)
//             .oauth2Error(OAuth2ErrorDetails.builder()
//                 .error(oauth2ErrorCode)
//                 .errorDescription(errorDescription)
//                 .errorUri(errorUri)
//                 .build())
//             .build();
//     }
    
//     /**
//      * Creates a validation error response.
//      * 
//      * @param validationException the validation exception
//      * @param path request path
//      * @return validation error response
//      */
//     public static ErrorResponse createValidationError(ValidationException validationException, String path) {
//         List<FieldError> fieldErrors = new ArrayList<>();
        
//         for (ValidationException.ValidationError validationError : validationException.getValidationErrors()) {
//             fieldErrors.add(FieldError.builder()
//                 .field(validationError.getField())
//                 .message(validationError.getMessage())
//                 .rejectedValue(validationError.getRejectedValue())
//                 .build());
//         }
        
//         return ErrorResponse.builder()
//             .errorId(generateErrorId())
//             .timestamp(LocalDateTime.now())
//             .status(HttpStatus.BAD_REQUEST.value())
//             .error("Validation Error")
//             .message(validationException.getMessage())
//             .path(path)
//             .fieldErrors(fieldErrors)
//             .build();
//     }
    
//     /**
//      * Creates an authentication error response.
//      * 
//      * @param message error message
//      * @param username username (optional)
//      * @param authenticationType authentication type (optional)
//      * @param path request path
//      * @return authentication error response
//      */
//     public static ErrorResponse createAuthenticationError(String message, String username, String authenticationType, String path) {
//         return ErrorResponse.builder()
//             .errorId(generateErrorId())
//             .timestamp(LocalDateTime.now())
//             .status(HttpStatus.UNAUTHORIZED.value())
//             .error("Authentication Error")
//             .message(message)
//             .path(path)
//             .authenticationContext(AuthenticationContextDetails.builder()
//                 .username(username)
//                 .authenticationType(authenticationType)
//                 .build())
//             .build();
//     }
    
//     /**
//      * Creates a device flow error response.
//      * 
//      * @param oauth2ErrorCode OAuth2 error code
//      * @param errorDescription error description
//      * @param deviceCode device code (optional)
//      * @param userCode user code (optional)
//      * @param path request path
//      * @return device flow error response
//      */
//     public static ErrorResponse createDeviceFlowError(String oauth2ErrorCode, String errorDescription, String deviceCode, String userCode, String path) {
//         return ErrorResponse.builder()
//             .errorId(generateErrorId())
//             .timestamp(LocalDateTime.now())
//             .status(HttpStatus.BAD_REQUEST.value())
//             .error("Device Flow Error")
//             .message(errorDescription)
//             .path(path)
//             .oauth2Error(OAuth2ErrorDetails.builder()
//                 .error(oauth2ErrorCode)
//                 .errorDescription(errorDescription)
//                 .build())
//             .deviceContext(DeviceContextDetails.builder()
//                 .deviceCode(deviceCode)
//                 .userCode(userCode)
//                 .build())
//             .build();
//     }
    
//     /**
//      * Creates an invalid client error response.
//      * 
//      * @param errorDescription error description
//      * @param path request path
//      * @return invalid client error response
//      */
//     public static ErrorResponse createInvalidClientError(String errorDescription, String path) {
//         return createOAuth2Error(HttpStatus.UNAUTHORIZED, OAuth2ErrorCodes.INVALID_CLIENT, errorDescription, path);
//     }
    
//     /**
//      * Creates an invalid request error response.
//      * 
//      * @param errorDescription error description
//      * @param path request path
//      * @return invalid request error response
//      */
//     public static ErrorResponse createInvalidRequestError(String errorDescription, String path) {
//         return createOAuth2Error(HttpStatus.BAD_REQUEST, OAuth2ErrorCodes.INVALID_REQUEST, errorDescription, path);
//     }
    
//     /**
//      * Creates an access denied error response.
//      * 
//      * @param errorDescription error description
//      * @param path request path
//      * @return access denied error response
//      */
//     public static ErrorResponse createAccessDeniedError(String errorDescription, String path) {
//         return createOAuth2Error(HttpStatus.FORBIDDEN, OAuth2ErrorCodes.ACCESS_DENIED, errorDescription, path);
//     }
    
//     /**
//      * Creates a server error response.
//      * 
//      * @param errorDescription error description
//      * @param path request path
//      * @return server error response
//      */
//     public static ErrorResponse createServerError(String errorDescription, String path) {
//         return createOAuth2Error(HttpStatus.INTERNAL_SERVER_ERROR, OAuth2ErrorCodes.SERVER_ERROR, errorDescription, path);
//     }
    
//     /**
//      * Creates a temporarily unavailable error response.
//      * 
//      * @param errorDescription error description
//      * @param path request path
//      * @return temporarily unavailable error response
//      */
//     public static ErrorResponse createTemporarilyUnavailableError(String errorDescription, String path) {
//         return createOAuth2Error(HttpStatus.SERVICE_UNAVAILABLE, OAuth2ErrorCodes.TEMPORARILY_UNAVAILABLE, errorDescription, path);
//     }
    
//     /**
//      * Creates a not found error response.
//      * 
//      * @param message error message
//      * @param path request path
//      * @return not found error response
//      */
//     public static ErrorResponse createNotFoundError(String message, String path) {
//         return createGenericError(HttpStatus.NOT_FOUND, "Not Found", message, path);
//     }
    
//     /**
//      * Creates a method not allowed error response.
//      * 
//      * @param message error message
//      * @param path request path
//      * @return method not allowed error response
//      */
//     public static ErrorResponse createMethodNotAllowedError(String message, String path) {
//         return createGenericError(HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed", message, path);
//     }
    
//     /**
//      * Creates an unsupported media type error response.
//      * 
//      * @param message error message
//      * @param path request path
//      * @return unsupported media type error response
//      */
//     public static ErrorResponse createUnsupportedMediaTypeError(String message, String path) {
//         return createGenericError(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported Media Type", message, path);
//     }
    
//     /**
//      * Creates an internal server error response.
//      * 
//      * @param message error message
//      * @param path request path
//      * @return internal server error response
//      */
//     public static ErrorResponse createInternalServerError(String message, String path) {
//         return createGenericError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", message, path);
//     }
    
//     /**
//      * Generates a unique error ID for tracking purposes.
//      * 
//      * @return unique error ID
//      */
//     private static String generateErrorId() {
//         return UUID.randomUUID().toString();
//     }
// } 