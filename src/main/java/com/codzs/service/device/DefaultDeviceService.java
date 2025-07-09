package com.codzs.service.device;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.codzs.validation.ValidationService;

/**
 * Default implementation of {@link DeviceService}.
 * This service handles device flow operations including device code generation,
 * user code validation, and device flow management.
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
@Service
public class DefaultDeviceService implements DeviceService {

    private final Log logger = LogFactory.getLog(getClass());
    private final ValidationService validationService;
    
    public DefaultDeviceService(ValidationService validationService) {
        this.validationService = validationService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DeviceActivationResult processDeviceActivation(String userCode) {
        if (logger.isDebugEnabled()) {
            logger.debug("Processing device activation with user code: " + 
                (StringUtils.hasText(userCode) ? "[present]" : "[empty]"));
        }

        // If user code is provided, validate it and redirect to device verification endpoint
        if (StringUtils.hasText(userCode)) {
            // Validate user code format
            validationService.validateUserCode(userCode);
            
            String redirectUrl = "/oauth2/device_verification?user_code=" + userCode;
            
            if (logger.isDebugEnabled()) {
                logger.debug("Redirecting to device verification: " + redirectUrl);
            }
            
            return DeviceActivationResult.redirect(redirectUrl);
        }

        // Otherwise, show the device activation page
        if (logger.isDebugEnabled()) {
            logger.debug("Showing device activation page");
        }
        
        return DeviceActivationResult.view("device-activate");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String processDeviceActivationSuccess() {
        if (logger.isDebugEnabled()) {
            logger.debug("Processing device activation success");
        }
        
        return "device-activated";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String processDeviceSuccessCallback() {
        if (logger.isDebugEnabled()) {
            logger.debug("Processing device success callback");
        }
        
        return "device-activated";
    }
} 