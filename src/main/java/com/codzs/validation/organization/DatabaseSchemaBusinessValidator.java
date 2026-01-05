package com.codzs.validation.organization;

import com.codzs.entity.organization.DatabaseSchema;
import com.codzs.entity.organization.Organization;
import com.codzs.exception.bean.ValidationError;
import com.codzs.exception.type.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Business validator for DatabaseSchema operations within organizations.
 * Focuses on database schema business rules, uniqueness constraints, and schema management.
 * 
 * @author Codzs Team
 * @since 1.0
 */ 
@Component
public class DatabaseSchemaBusinessValidator {

    @Autowired
    public DatabaseSchemaBusinessValidator() {
    }

    // ========== ENTRY POINT METHODS FOR ORGANIZATION APIs ==========

    /**
     * Validates database schema addition to existing organization.
     * Entry point for: POST /api/v1/organizations/{id}/database-schemas
     */
    public void validateDatabaseSchemaAddition(DatabaseSchema schema, boolean isSchemaNameExists, boolean isServiceExists) {
        List<ValidationError> errors = new ArrayList<>();
        validateSchemaNameUniqueness(schema.getSchemaName(), isSchemaNameExists, errors);
        validateServiceUniqueness(schema.getForService(), isServiceExists, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Database schema addition validation failed", errors);
        }
    }

    /**
     * Validates database schema update request.
     * Entry point for: PUT /api/v1/organizations/{id}/database-schemas/{schemaId}
     */
    public void validateDatabaseSchemaUpdate(DatabaseSchema schema, boolean isSchemaNameExists, boolean isServiceExists) {
        List<ValidationError> errors = new ArrayList<>();
        validateSchemaNameUniqueness(schema.getSchemaName(), isSchemaNameExists, errors);
        validateServiceUniqueness(schema.getForService(), isServiceExists, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Database schema update validation failed", errors);
        }
    }

    /**
     * Validates database schema removal request.
     * Entry point for: DELETE /api/v1/organizations/{id}/database-schemas/{schemaId}
     */
    public void validateDatabaseSchemaRemoval(Organization organization, DatabaseSchema schema) {
        List<ValidationError> errors = new ArrayList<>();
        validateDatabaseSchemaRemovalFlow(organization, schema, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Database schema removal validation failed", errors);
        }
    }

    // ========== CORE VALIDATION METHODS ==========

    private void validateDatabaseSchemaRemovalFlow(Organization organization, DatabaseSchema schema, 
                                                  List<ValidationError> errors) {
        // Validate that at least one schema will remain after removal
        List<DatabaseSchema> schemas = organization.getDatabase().getSchemas();
        if (schemas.size() <= 1) {
            errors.add(
                ValidationError
                    .builder()
                    .field("schemaId")
                    .rejectedValue(schemas.size())
                    .message("Cannot remove last database schema - at least one schema is required")
                    .build()
            );
        }
    }

    // ========== FIELD VALIDATION METHODS ==========

    private void validateSchemaNameUniqueness(String schemaName, boolean isSchemaNameExists,
                                             List<ValidationError> errors) {
        if (StringUtils.hasText(schemaName) && isSchemaNameExists) {
            errors.add(
                ValidationError
                    .builder()
                    .field("schemaName")
                    .rejectedValue(schemaName)
                    .message("Database schema name already exists: " + schemaName)
                    .build()
            );
        }
    }

    private void validateServiceUniqueness(String forService, boolean isServiceExists,
                                         List<ValidationError> errors) {
        if (StringUtils.hasText(forService) && isServiceExists) {
            errors.add(
                ValidationError
                    .builder()
                    .field("forService")
                    .rejectedValue(forService)
                    .message("Service type already exists in this organization: " + forService)
                    .build()
            );
        }
    }
}