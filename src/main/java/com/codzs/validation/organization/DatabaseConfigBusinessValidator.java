package com.codzs.validation.organization;

import com.codzs.entity.organization.DatabaseConfig;
import com.codzs.entity.organization.DatabaseSchema;
import com.codzs.entity.organization.Organization;
import com.codzs.exception.validation.ValidationException;
import com.codzs.service.organization.DatabaseConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Business validator for DatabaseConfig operations within organizations.
 * Focuses on database configuration business rules, schema validation, and connectivity constraints.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Component
public class DatabaseConfigBusinessValidator {

    private final DatabaseConfigService databaseConfigService;

    @Autowired
    public DatabaseConfigBusinessValidator(DatabaseConfigService databaseConfigService) {
        this.databaseConfigService = databaseConfigService;
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

    /**
     * Validates database schema addition to existing organization.
     * Entry point for: POST /api/v1/organizations/{id}/database-schemas
     */
    public void validateDatabaseSchemaAddition(Organization organization, DatabaseSchema schema) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        validateDatabaseSchemaAdditionFlow(organization, schema, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Database schema addition validation failed", errors);
        }
    }

    /**
     * Validates database schema update request.
     * Entry point for: PUT /api/v1/organizations/{id}/database-schemas/{schemaId}
     */
    public void validateDatabaseSchemaUpdate(Organization organization, DatabaseSchema schema) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        validateDatabaseSchemaUpdateFlow(organization, schema, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Database schema update validation failed", errors);
        }
    }

    /**
     * Validates database schema removal request.
     * Entry point for: DELETE /api/v1/organizations/{id}/database-schemas/{schemaId}
     */
    public void validateDatabaseSchemaRemoval(Organization organization, DatabaseSchema schema) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        validateDatabaseSchemaRemovalFlow(organization, schema, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Database schema removal validation failed", errors);
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

    private void validateDatabaseSchemaAdditionFlow(Organization organization, DatabaseSchema schema, 
                                                   List<ValidationException.ValidationError> errors) {
        validateSchemaNameUniqueness(organization.getId(), schema.getSchemaName(), null, errors);
    }

    private void validateDatabaseSchemaUpdateFlow(Organization organization, DatabaseSchema schema, 
                                                 List<ValidationException.ValidationError> errors) {
        validateSchemaNameUniqueness(organization.getId(), schema.getSchemaName(), schema.getId(), errors);
    }

    private void validateDatabaseSchemaRemovalFlow(Organization organization, DatabaseSchema schema, 
                                                  List<ValidationException.ValidationError> errors) {
        // Validate that at least one schema will remain after removal
        List<DatabaseSchema> schemas = databaseConfigService.getDatabaseSchemas(organization.getId());
        if (schemas.size() <= 1) {
            errors.add(new ValidationException.ValidationError("schemaId", 
                "Cannot remove last database schema - at least one schema is required"));
        }
    }

    // ========== FIELD VALIDATION METHODS ==========

    private void validateConnectionStringFormat(String connectionString, 
                                               List<ValidationException.ValidationError> errors) {
        if (!StringUtils.hasText(connectionString)) {
            return;
        }

        // Basic connection string format validation
        if (!connectionString.toLowerCase().startsWith("mongodb://") && 
            !connectionString.toLowerCase().startsWith("mongodb+srv://")) {
            errors.add(new ValidationException.ValidationError("connectionString", 
                "Connection string must start with 'mongodb://' or 'mongodb+srv://'"));
        }

        // Check for invalid characters or patterns
        if (connectionString.contains("localhost") || connectionString.contains("127.0.0.1")) {
            errors.add(new ValidationException.ValidationError("connectionString", 
                "Connection string cannot use localhost or local IP addresses"));
        }
    }

    private void validateCertificateFormat(String certificate, List<ValidationException.ValidationError> errors) {
        if (!StringUtils.hasText(certificate)) {
            return;
        }

        // Basic certificate format validation
        if (!certificate.contains("-----BEGIN CERTIFICATE-----") || 
            !certificate.contains("-----END CERTIFICATE-----")) {
            errors.add(new ValidationException.ValidationError("certificate", 
                "Invalid certificate format - must be a valid PEM certificate"));
        }
    }

    private void validateSchemaNameUniqueness(String organizationId, String schemaName, String excludeSchemaId,
                                             List<ValidationException.ValidationError> errors) {
        if (StringUtils.hasText(schemaName) && 
            databaseConfigService.isSchemaNameExists(organizationId, schemaName, excludeSchemaId)) {
            errors.add(new ValidationException.ValidationError("schemaName", 
                "Database schema name already exists: " + schemaName));
        }
    }
}