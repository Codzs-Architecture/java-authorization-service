package com.codzs.validation.organization;

import com.codzs.constant.organization.OrganizationConstants;
import com.codzs.entity.organization.Organization;
import com.codzs.framework.exception.type.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Business validator for DatabaseConfig operations within organizations.
 * Focuses on database configuration business rules and connectivity constraints.
 * 
 * @author Codzs Team
 * @since 1.0
 */ 
@Component
public class DatabaseConfigBusinessValidator {

    @Autowired
    public DatabaseConfigBusinessValidator() {
    }

    // ========== ENTRY POINT METHODS FOR ORGANIZATION APIs ==========

    /**
     * Validates database config update for service layer.
     * Entry point for: PUT /api/v1/organizations/{id}/database-config
     */
    public void validateDatabaseConfigUpdate(Organization organization, String connectionString, String certificate) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        validateDatabaseConfigUpdateFlow(organization, connectionString, certificate, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Database config update validation failed", errors);
        }
    }

    // ========== CORE VALIDATION METHODS ==========

    private void validateDatabaseConfigUpdateFlow(Organization organization, String connectionString, String certificate, 
                                                 List<ValidationException.ValidationError> errors) {
        // For config updates, we only validate the config fields that can be updated
        // We don't validate schemas here since they are managed separately
        validateConnectionStringFormat(connectionString, errors);
        validateCertificateFormat(certificate, errors);
    }

    // ========== FIELD VALIDATION METHODS ==========

    private void validateConnectionStringFormat(String connectionString, 
                                               List<ValidationException.ValidationError> errors) {
        if (!StringUtils.hasText(connectionString)) {
            return;
        }

        // Basic connection string format validation
        if (!connectionString.toLowerCase().startsWith(OrganizationConstants.MONGODB_PREFIX) && 
            !connectionString.toLowerCase().startsWith(OrganizationConstants.MONGODB_SRV_PREFIX)) {
            errors.add(new ValidationException.ValidationError("connectionString", 
                String.format("Connection string must start with '%s' or '%s'", 
                    OrganizationConstants.MONGODB_PREFIX, OrganizationConstants.MONGODB_SRV_PREFIX)));
        }

        // Check for invalid characters or patterns
        if (connectionString.contains(OrganizationConstants.LOCALHOST) || 
            connectionString.contains(OrganizationConstants.LOCALHOST_IP)) {
            errors.add(new ValidationException.ValidationError("connectionString", 
                "Connection string cannot use localhost or local IP addresses"));
        }
    }

    private void validateCertificateFormat(String certificate, List<ValidationException.ValidationError> errors) {
        if (!StringUtils.hasText(certificate)) {
            return;
        }

        // Basic certificate format validation
        if (!certificate.contains(OrganizationConstants.CERTIFICATE_BEGIN_MARKER) || 
            !certificate.contains(OrganizationConstants.CERTIFICATE_END_MARKER)) {
            errors.add(new ValidationException.ValidationError("certificate", 
                "Invalid certificate format - must be a valid PEM certificate"));
        }
    }
}