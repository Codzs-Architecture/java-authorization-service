package com.codzs.framework.logger.filter;
// package com.codzs.framework.filter;

// import com.codzs.framework.constant.CommonConstants;
// import com.codzs.framework.context.CorrelationIdContext;
// import jakarta.servlet.*;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.slf4j.MDC;
// import org.springframework.core.Ordered;
// import org.springframework.core.annotation.Order;
// import org.springframework.stereotype.Component;

// import java.io.IOException;
// import java.util.Arrays;
// import java.util.List;

// /**
//  * Servlet filter that manages correlation IDs for HTTP requests.
//  * This filter:
//  * 1. Extracts correlation ID from request headers or generates a new one
//  * 2. Sets the correlation ID in ThreadLocal context for the request
//  * 3. Adds correlation ID to MDC for logging correlation
//  * 4. Adds correlation ID to response headers for client tracking
//  * 5. Ensures proper cleanup after request completion
//  * 
//  * The filter runs with highest priority to ensure correlation ID is available
//  * for all subsequent filters and processing.
//  * 
//  * @author Codzs Team
//  * @since 1.0
//  */
// @Component
// @Order(Ordered.HIGHEST_PRECEDENCE)
// public class CorrelationIdFilter implements Filter {
    
//     private static final Logger logger = LoggerFactory.getLogger(CorrelationIdFilter.class);
    
//     /**
//      * List of possible header names for correlation ID.
//      * Checked in order of preference.
//      */
//     private static final List<String> CORRELATION_ID_HEADERS = Arrays.asList(
//         CommonConstants.CORRELATION_ID_HEADER,
//         "X-Request-ID",
//         "X-Trace-ID",
//         "Request-ID",
//         "Trace-ID"
//     );
    
//     /**
//      * MDC key for correlation ID used in logging.
//      */
//     private static final String MDC_CORRELATION_ID_KEY = "correlationId";
    
//     /**
//      * Response header name for correlation ID.
//      */
//     private static final String RESPONSE_CORRELATION_ID_HEADER = CommonConstants.CORRELATION_ID_HEADER;
    
//     @Override
//     public void init(FilterConfig filterConfig) throws ServletException {
//         logger.info("Initializing CorrelationIdFilter");
//     }
    
//     @Override
//     public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//             throws IOException, ServletException {
        
//         if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
//             chain.doFilter(request, response);
//             return;
//         }
        
//         HttpServletRequest httpRequest = (HttpServletRequest) request;
//         HttpServletResponse httpResponse = (HttpServletResponse) response;
        
//         // Extract or generate correlation ID
//         String correlationId = extractOrGenerateCorrelationId(httpRequest);
        
//         try {
//             // Set correlation ID in ThreadLocal context
//             CorrelationIdContext.setCorrelationId(correlationId);
            
//             // Add to MDC for logging
//             MDC.put(MDC_CORRELATION_ID_KEY, correlationId);
            
//             // Add to response header for client tracking
//             httpResponse.setHeader(RESPONSE_CORRELATION_ID_HEADER, correlationId);
            
//             logger.debug("Processing request with correlation ID: {} for URI: {}", 
//                     correlationId, httpRequest.getRequestURI());
            
//             // Continue with the filter chain
//             chain.doFilter(request, response);
            
//         } finally {
//             // Clean up ThreadLocal and MDC to prevent memory leaks
//             try {
//                 CorrelationIdContext.clear();
//                 MDC.clear();
//                 logger.debug("Cleaned up correlation ID: {}", correlationId);
//             } catch (Exception e) {
//                 logger.warn("Error during correlation ID cleanup", e);
//             }
//         }
//     }
    
//     /**
//      * Extracts correlation ID from request headers or generates a new one.
//      * Checks multiple possible header names in order of preference.
//      * 
//      * @param request the HTTP request
//      * @return the correlation ID (extracted or generated)
//      */
//     private String extractOrGenerateCorrelationId(HttpServletRequest request) {
//         // Try to extract from various possible headers
//         for (String headerName : CORRELATION_ID_HEADERS) {
//             String correlationId = request.getHeader(headerName);
//             if (isValidCorrelationId(correlationId)) {
//                 logger.debug("Extracted correlation ID from header {}: {}", headerName, correlationId);
//                 return correlationId;
//             }
//         }
        
//         // Generate new correlation ID if not found in headers
//         String generatedId = CorrelationIdContext.generateCorrelationId();
//         logger.debug("Generated new correlation ID: {} for request: {}", 
//                 generatedId, request.getRequestURI());
//         return generatedId;
//     }
    
//     /**
//      * Validates if a correlation ID is valid (not null, not empty, reasonable length).
//      * 
//      * @param correlationId the correlation ID to validate
//      * @return true if valid, false otherwise
//      */
//     private boolean isValidCorrelationId(String correlationId) {
//         if (correlationId == null || correlationId.trim().isEmpty()) {
//             return false;
//         }
        
//         String trimmed = correlationId.trim();
        
//         // Check reasonable length constraints
//         if (trimmed.length() < 3 || trimmed.length() > 100) {
//             logger.debug("Invalid correlation ID length: {}", trimmed.length());
//             return false;
//         }
        
//         // Check for valid characters (alphanumeric, dash, underscore)
//         if (!trimmed.matches("^[a-zA-Z0-9_-]+$")) {
//             logger.debug("Invalid correlation ID format: {}", trimmed);
//             return false;
//         }
        
//         return true;
//     }
    
//     @Override
//     public void destroy() {
//         logger.info("Destroying CorrelationIdFilter");
//     }
// }