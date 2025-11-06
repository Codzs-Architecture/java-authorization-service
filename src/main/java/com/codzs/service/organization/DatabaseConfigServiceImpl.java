package com.codzs.service.organization;

import com.codzs.entity.organization.DatabaseConfig;
import com.codzs.entity.organization.DatabaseSchema;
import com.codzs.entity.organization.Organization;
import com.codzs.repository.organization.DatabaseConfigRepository;
import com.codzs.repository.organization.OrganizationRepository;
import com.codzs.validation.organization.DatabaseConfigBusinessValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.codzs.exception.validation.ValidationException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
public class DatabaseConfigServiceImpl extends BaseOrganizationServiceImpl implements DatabaseConfigService {

    private final DatabaseConfigRepository databaseConfigRepository;
    private final DatabaseConfigBusinessValidator databaseConfigBusinessValidator;
    
    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Autowired
    public DatabaseConfigServiceImpl(DatabaseConfigRepository databaseConfigRepository,
                                   DatabaseConfigBusinessValidator databaseConfigBusinessValidator,
                                   OrganizationRepository organizationRepository, 
                                   ObjectMapper objectMapper) {
        super(organizationRepository, objectMapper);
        this.databaseConfigRepository = databaseConfigRepository;
        this.databaseConfigBusinessValidator = databaseConfigBusinessValidator;
    }

    // ========== API FLOW METHODS ==========

    @Override
    @Transactional
    public Organization updateDatabaseConfig(String organizationId, String connectionString, String certificate) {
        log.debug("Updating database config for organization ID: {}", organizationId);
        
        // Get organization and validate it exists
        Organization organization = getOrganizationAndValidate(organizationId);
        
        // Business validation for database configuration
        databaseConfigBusinessValidator.validateDatabaseConfigUpdate(organization, connectionString, certificate);
        
        // Use specific MongoDB operations to update only database config fields (preserving schemas)
        updateDatabaseConfigFields(organizationId, connectionString, certificate);
        
        log.info("Updated database config for organization ID: {}", organizationId);
        
        // Return updated organization
        return getOrganizationAndValidate(organizationId);
    }

    @Override
    public Optional<DatabaseConfig> getDatabaseConfig(String organizationId) {
        log.debug("Getting database config for organization ID: {}", organizationId);
        
        return getOrgById(organizationId)
                .map(Organization::getDatabase)
                .filter(database -> database != null);
    }

    @Override
    @Transactional
    public List<DatabaseSchema> addDatabaseSchema(String organizationId, DatabaseSchema schema) {
        log.debug("Adding database schema for organization ID: {}", organizationId);
        
        // Get organization and validate it exists
        Organization organization = getOrganizationAndValidate(organizationId);
        
        // Validate database config exists
        if (organization.getDatabase() == null) {
            throw new ValidationException("Database configuration must exist before adding schemas");
        }
        
        // Business validation for schema addition
        boolean isSchemaNameExists = this.isSchemaNameExists(organizationId, schema.getSchemaName(), null);
        databaseConfigBusinessValidator.validateDatabaseSchemaAddition(schema, isSchemaNameExists);
        
        // Set schema ID if not present
        if (!StringUtils.hasText(schema.getId())) {
            schema.setId(UUID.randomUUID().toString());
        }
        
        // Apply schema addition business logic
        applySchemaAdditionBusinessLogic(organization, schema);
        
        // Use MongoDB array operation to add schema directly
        databaseConfigRepository.addDatabaseSchema(organizationId, schema);
        
        log.info("Added database schema {} for organization ID: {}", 
                schema.getSchemaName(), organizationId);
        
        // Return updated list of all schemas
        return getDatabaseSchemas(organizationId);
    }

    @Override
    @Transactional
    public Organization updateDatabaseSchema(String organizationId, DatabaseSchema schema) {
        log.debug("Updating database schema {} for organization ID: {}", schema.getId(), organizationId);
        
        // Get organization and validate it exists
        Organization organization = getOrganizationAndValidate(organizationId);
        
        // Find schema to update
        DatabaseSchema existingSchema = findSchemaById(organization, schema.getId());
        if (existingSchema == null) {
            throw new ValidationException("Database schema not found with ID: " + schema.getId());
        }
        
        // Business validation for schema update
        boolean isSchemaNameExists = this.isSchemaNameExists(organizationId, schema.getSchemaName(), schema.getId());
        databaseConfigBusinessValidator.validateDatabaseSchemaUpdate(schema, isSchemaNameExists);
        
        // Use MongoDB array operation to update entire schema in one go
        databaseConfigRepository.updateDatabaseSchema(organizationId, schema.getId(), schema);
        
        log.info("Updated database schema {} for organization ID: {}", schema.getId(), organizationId);
        
        // Return updated organization
        return getOrganizationAndValidate(organizationId);
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
        log.debug("Getting all database schemas for organization ID: {}", organizationId);
        
        // Delegate to the listDatabaseSchemas method without any filters
        return listDatabaseSchemas(organizationId, null, null);
    }

    @Override
    public List<DatabaseSchema> listDatabaseSchemas(String organizationId, String forService, String status) {
        log.debug("Listing database schemas for organization ID: {}, forService: {}, status: {}", 
                 organizationId, forService, status);
        
        return getDatabaseConfig(organizationId)
                .map(DatabaseConfig::getSchemas)
                .filter(schemas -> schemas != null)
                .map(schemas -> {
                    // Apply filtering if parameters are provided
                    List<DatabaseSchema> filteredSchemas = schemas;
                    
                    if (forService != null && !forService.trim().isEmpty()) {
                        filteredSchemas = filteredSchemas.stream()
                            .filter(schema -> forService.equalsIgnoreCase(schema.getForService()))
                            .toList();
                    }
                    
                    // Note: Status filtering is not implemented as DatabaseSchema entity doesn't have a status field
                    // TODO: Implement status field in DatabaseSchema entity if needed
                    if (status != null && !status.trim().isEmpty()) {
                        log.warn("Status filtering requested but not implemented for DatabaseSchema: {}", status);
                    }
                    
                    log.debug("Returned {} filtered schemas for organization ID: {}", filteredSchemas.size(), organizationId);
                    return filteredSchemas;
                })
                .orElse(new ArrayList<>());
    }

    @Override
    public Optional<DatabaseSchema> getDatabaseSchema(String organizationId, String schemaId) {
        log.debug("Getting database schema {} for organization ID: {}", schemaId, organizationId);
        
        return getOrgById(organizationId)
                .map(organization -> findSchemaById(organization, schemaId))
                .filter(schema -> schema != null);
    }

    @Override
    public boolean testDatabaseConnection(String organizationId) {
        log.debug("Testing database connection for organization ID: {}", organizationId);
        
        return getDatabaseConfig(organizationId)
                .map(databaseConfig -> {
                    // TODO: Implement actual database connection testing
                    // This would involve creating a connection with the provided connection string
                    // and certificate, and testing basic connectivity
                    log.debug("Database connection test not implemented yet for organization ID: {}", organizationId);
                    
                    return true; // Placeholder - assume connection is valid
                })
                .orElseGet(() -> {
                    log.warn("No database configuration found for organization ID: {}", organizationId);
                    return false;
                });
    }

    // ========== UTILITY METHODS ==========

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
            schemaName.append(activeProfile);
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
            String generatedName = generateSchemaName(organization.getAbbr(), schema.getForService(), activeProfile);
            schema.setSchemaName(generatedName);
        }
    }

    private void updateDatabaseConfigFields(String organizationId, String connectionString, String certificate) {
        // Use specific MongoDB operations to update only the database config fields (excluding schemas)
        if (StringUtils.hasText(connectionString)) {
            databaseConfigRepository.updateDatabaseConnectionString(organizationId, connectionString);
        }
        
        if (StringUtils.hasText(certificate)) {
            databaseConfigRepository.updateDatabaseCertificate(organizationId, certificate);
        }
        
        log.debug("Updated database config fields for organization {}", organizationId);
    }
}