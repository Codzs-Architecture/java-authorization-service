package com.codzs.service.error;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Default implementation of {@link ErrorService}.
 * This service handles error operations including error creation,
 * formatting, and response generation.
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
@Service
public class DefaultErrorService implements ErrorService {

    private final Log logger = LogFactory.getLog(getClass());
    
    private static final String ACCESS_DENIED_PREFIX = "[access_denied]";

    /**
     * {@inheritDoc}
     */
    @Override
    public ErrorModel processError(HttpServletRequest request) {
        String errorMessage = extractErrorMessage(request);
        
        if (logger.isDebugEnabled()) {
            logger.debug("Processing error with message: " + errorMessage);
        }

        if (isAccessDeniedError(errorMessage)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Error identified as access denied");
            }
            return ErrorModel.accessDenied();
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Error identified as generic error");
            }
            return ErrorModel.genericError(errorMessage);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String extractErrorMessage(HttpServletRequest request) {
        String errorMessage = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        String extractedMessage = StringUtils.hasText(errorMessage) ? errorMessage : "";
        
        if (logger.isDebugEnabled()) {
            logger.debug("Extracted error message: " + extractedMessage);
        }
        
        return extractedMessage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAccessDeniedError(String errorMessage) {
        boolean isAccessDenied = StringUtils.hasText(errorMessage) && 
                                 errorMessage.startsWith(ACCESS_DENIED_PREFIX);
        
        if (logger.isDebugEnabled()) {
            logger.debug("Access denied check result: " + isAccessDenied);
        }
        
        return isAccessDenied;
    }
} 