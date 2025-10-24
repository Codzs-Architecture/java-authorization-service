package com.codzs.service.organization;

import com.codzs.entity.organization.DatabaseConfig;
import com.codzs.entity.organization.DatabaseSchema;
import com.codzs.entity.organization.Organization;
import com.codzs.exception.validation.ValidationException;
import com.codzs.repository.organization.DatabaseConfigRepository;
import com.codzs.validation.organization.DatabaseConfigBusinessValidator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service implementation for DatabaseConfig-related business operations.
 * Manages database configuration and schema operations for organizations
 * with proper business validation and transaction management.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class DatabaseConfigServiceImpl implements DatabaseConfigService {

    private final DatabaseConfigRepository databaseConfigRepository;
    private final OrganizationService organizationService;
    private final DatabaseConfigBusinessValidator databaseConfigBusinessValidator;

    @Autowired
    public DatabaseConfigServiceImpl(DatabaseConfigRepository databaseConfigRepository,
                                   OrganizationService organizationService,
                                   DatabaseConfigBusinessValidator databaseConfigBusinessValidator) {
        this.databaseConfigRepository = databaseConfigRepository;
        this.organizationService = organizationService;
        this.databaseConfigBusinessValidator = databaseConfigBusinessValidator;
    }

    // ========== API FLOW METHODS ==========

    @Override
    @Transactional
    public Organization updateDatabaseConfig(Organization organization, DatabaseConfig databaseConfig) {
        log.debug("Updating database config for organization ID: {}", organization.getId());
        
        // Business validation for database configuration
        databaseConfigBusinessValidator.validateDatabaseConfigUpdate(organization, databaseConfig);
        
        // Use specific MongoDB operations to update only database config fields (preserving schemas)
        updateDatabaseConfigFields(organization.getId(), databaseConfig);
        
        log.info("Updated database config for organization ID: {}", organization.getId());
        
        // Return updated organization
        return getOrganizationAndValidate(organization.getId());
    }

    @Override
    public DatabaseConfig getDatabaseConfig(String organizationId) {
        log.debug("Getting database config for organization ID: {}", organizationId);
        
        Organization organization = organizationService.findById(organizationId);
        if (organization == null) {
            log.warn("Organization not found with ID: {}", organizationId);
            return null;
        }
        
        return organization.getDatabase();
    }

    @Override
    @Transactional
    public Organization addDatabaseSchema(Organization organization, DatabaseSchema schema) {
        log.debug("Adding database schema for organization ID: {}", organization.getId());
        
        // Validate database config exists
        if (organization.getDatabase() == null) {
            throw new ValidationException("Database configuration must exist before adding schemas");
        }
        
        // Business validation for schema addition
        databaseConfigBusinessValidator.validateDatabaseSchemaAddition(organization, schema);
        
        // Set schema ID if not present
        if (!StringUtils.hasText(schema.getId())) {
            schema.setId(UUID.randomUUID().toString());
        }
        
        // Apply schema addition business logic
        applySchemaAdditionBusinessLogic(organization, schema);
        
        // Use MongoDB array operation to add schema directly
        databaseConfigRepository.addDatabaseSchema(organization.getId(), schema);
        
        log.info("Added database schema {} for organization ID: {}", 
                schema.getSchemaName(), organization.getId());
        
        // Return updated organization
        return getOrganizationAndValidate(organization.getId());
    }

    @Override
    @Transactional
    public Organization updateDatabaseSchema(Organization organization, DatabaseSchema schema) {
        log.debug("Updating database schema {} for organization ID: {}", schema.getId(), organization.getId());
        
        // Find schema to update
        DatabaseSchema existingSchema = findSchemaById(organization, schema.getId());
        if (existingSchema == null) {
            throw new ValidationException("Database schema not found with ID: " + schema.getId());
        }
        
        // Business validation for schema update
        databaseConfigBusinessValidator.validateDatabaseSchemaUpdate(organization, schema);
        
        // Use MongoDB array operations to update specific schema fields
        updateSchemaFields(organization.getId(), schema.getId(), schema);
        
        log.info("Updated database schema {} for organization ID: {}", schema.getId(), organization.getId());
        
        // Return updated organization
        return getOrganizationAndValidate(organization.getId());
    }

    @Override
    @Transactional
    public Organization removeDatabaseSchema(String organizationId, String schemaId) {
        log.debug("Removing database schema {} for organization ID: {}", schemaId, organizationId);
        
        // Get organization and validate it exists
        Organization organization = getOrganizationAndValidate(organizationId);
        
        // Find schema to remove
        DatabaseSchema schemaToRemove = findSchemaById(organization, schemaId);
        if (schemaToRemove == null) {
            throw new ValidationException("Database schema not found with ID: " + schemaId);
        }
        
        // Business validation for schema removal
        databaseConfigBusinessValidator.validateDatabaseSchemaRemoval(organization, schemaToRemove);
        
        // Use MongoDB array operation to remove schema directly
        databaseConfigRepository.removeDatabaseSchema(organizationId, schemaId);
        
        log.info("Removed database schema {} for organization ID: {}", schemaId, organizationId);
        
        // Return updated organization
        return getOrganizationAndValidate(organizationId);
    }

    @Override
    public List<DatabaseSchema> getDatabaseSchemas(String organizationId) {
        log.debug("Getting database schemas for organization ID: {}", organizationId);
        
        DatabaseConfig databaseConfig = getDatabaseConfig(organizationId);
        if (databaseConfig == null || databaseConfig.getSchemas() == null) {
            return new ArrayList<>();
        }
        
        return databaseConfig.getSchemas();
    }

    @Override
    public DatabaseSchema getDatabaseSchema(String organizationId, String schemaId) {
        log.debug("Getting database schema {} for organization ID: {}", schemaId, organizationId);
        
        Organization organization = organizationService.findById(organizationId);
        if (organization == null) {
            log.warn("Organization not found with ID: {}", organizationId);
            return null;
        }
        
        return findSchemaById(organization, schemaId);
    }

    @Override
    public boolean testDatabaseConnection(String organizationId) {
        log.debug("Testing database connection for organization ID: {}", organizationId);
        
        DatabaseConfig databaseConfig = getDatabaseConfig(organizationId);
        if (databaseConfig == null) {
            log.warn("No database configuration found for organization ID: {}", organizationId);
            return false;
        }
        
        // TODO: Implement actual database connection testing
        // This would involve creating a connection with the provided connection string
        // and certificate, and testing basic connectivity
        log.debug("Database connection test not implemented yet for organization ID: {}", organizationId);
        
        return true; // Placeholder - assume connection is valid
    }

    // ========== UTILITY METHODS ==========

    @Override
    public boolean validateDatabaseConfig(String organizationId, DatabaseConfig databaseConfig) {
        // Basic validation is handled by entity annotations (@NotBlank, @NotEmpty)
        // This method can focus on business-specific validation if needed
        return databaseConfig != null;
    }

    @Override
    public String generateSchemaName(String organizationAbbr, String serviceType, String environment) {
        StringBuilder schemaName = new StringBuilder("codzs_");
        
        if (StringUtils.hasText(organizationAbbr)) {
            schemaName.append(organizationAbbr.toLowerCase()).append("_");
        }
        
        if (StringUtils.hasText(serviceType)) {
            schemaName.append(serviceType.toLowerCase()).append("_");
        }
        
        if (StringUtils.hasText(environment)) {
            schemaName.append(environment.toLowerCase());
        } else {
            schemaName.append("dev");
        }
        
        return schemaName.toString();
    }

    @Override
    public boolean isSchemaNameExists(String organizationId, String schemaName, String excludeSchemaId) {
        if (!StringUtils.hasText(schemaName)) {
            return false;
        }
        
        List<DatabaseSchema> schemas = getDatabaseSchemas(organizationId);
        
        return schemas.stream()
                .filter(schema -> StringUtils.hasText(excludeSchemaId) ? 
                        !schema.getId().equals(excludeSchemaId) : true)
                .anyMatch(schema -> schemaName.equalsIgnoreCase(schema.getSchemaName()));
    }

    // ========== PRIVATE HELPER METHODS ==========

    private Organization getOrganizationAndValidate(String organizationId) {
        Organization organization = organizationService.findById(organizationId);
        if (organization == null) {
            throw new ValidationException("Organization not found with ID: " + organizationId);
        }
        return organization;
    }

    private DatabaseSchema findSchemaById(Organization organization, String schemaId) {
        if (organization.getDatabase() == null || organization.getDatabase().getSchemas() == null) {
            return null;
        }
        
        return organization.getDatabase().getSchemas().stream()
                .filter(schema -> schemaId.equals(schema.getId()))
                .findFirst()
                .orElse(null);
    }


    private void applySchemaAdditionBusinessLogic(Organization organization, DatabaseSchema schema) {
        // Generate schema name if not provided
        if (!StringUtils.hasText(schema.getSchemaName())) {
            String generatedName = generateSchemaName(organization.getAbbr(), schema.getForService(), "dev");
            schema.setSchemaName(generatedName);
        }
    }

    private void updateDatabaseConfigFields(String organizationId, DatabaseConfig databaseConfig) {
        // Use specific MongoDB operations to update only the database config fields (excluding schemas)
        if (StringUtils.hasText(databaseConfig.getConnectionString())) {
            databaseConfigRepository.updateDatabaseConnectionString(organizationId, databaseConfig.getConnectionString());
        }
        
        if (StringUtils.hasText(databaseConfig.getCertificate())) {
            databaseConfigRepository.updateDatabaseCertificate(organizationId, databaseConfig.getCertificate());
        }
        
        log.debug("Updated database config fields for organization {}", organizationId);
    }

    private void updateSchemaFields(String organizationId, String schemaId, DatabaseSchema updatedSchema) {
        // Use specific MongoDB operations to update only the schema fields that are provided
        if (StringUtils.hasText(updatedSchema.getSchemaName())) {
            databaseConfigRepository.updateDatabaseSchemaName(organizationId, schemaId, updatedSchema.getSchemaName());
        }
        
        if (StringUtils.hasText(updatedSchema.getForService())) {
            databaseConfigRepository.updateDatabaseSchemaService(organizationId, schemaId, updatedSchema.getForService());
        }
        
        if (StringUtils.hasText(updatedSchema.getDescription())) {
            databaseConfigRepository.updateDatabaseSchemaDescription(organizationId, schemaId, updatedSchema.getDescription());
        }
        
        log.debug("Updated schema fields for schema {} in organization {}", schemaId, organizationId);
    }

}