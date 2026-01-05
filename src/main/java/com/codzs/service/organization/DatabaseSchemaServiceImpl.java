package com.codzs.service.organization;

import com.codzs.entity.organization.DatabaseConfig;
import com.codzs.entity.organization.DatabaseSchema;
import com.codzs.entity.organization.Organization;
import com.codzs.exception.type.ValidationException;
import com.codzs.repository.organization.DatabaseConfigRepository;
import com.codzs.repository.organization.OrganizationRepository;
import com.codzs.util.organization.DatabaseSchemaUtil;
import com.codzs.validation.organization.DatabaseSchemaBusinessValidator;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for DatabaseSchema-related business operations.
 * Manages database schema operations for organizations
 * with proper business validation and transaction management.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class DatabaseSchemaServiceImpl extends BaseOrganizationServiceImpl implements DatabaseSchemaService {

    private final DatabaseConfigRepository databaseConfigRepository;
    private final DatabaseSchemaBusinessValidator databaseSchemaBusinessValidator;
    
    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Autowired
    public DatabaseSchemaServiceImpl(DatabaseConfigRepository databaseConfigRepository,
                                   DatabaseSchemaBusinessValidator databaseSchemaBusinessValidator,
                                   OrganizationRepository organizationRepository, 
                                   ObjectMapper objectMapper) {
        super(organizationRepository, objectMapper);
        this.databaseConfigRepository = databaseConfigRepository;
        this.databaseSchemaBusinessValidator = databaseSchemaBusinessValidator;
    }

    // ========== API FLOW METHODS ==========

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
        boolean isServiceExists = this.isServiceExists(organizationId, schema.getForService(), null);
        databaseSchemaBusinessValidator.validateDatabaseSchemaAddition(schema, isSchemaNameExists, isServiceExists);
        
        // Set schema ID if not present
        if (!StringUtils.hasText(schema.getId())) {
            schema.setId(ObjectId.get().toString());
        }
        
        // Apply schema addition business logic
        DatabaseSchemaUtil.applySchemaBusinessLogic(organization, schema, activeProfile);
        
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
        DatabaseSchema existingSchema = DatabaseSchemaUtil.findSchemaById(organization, schema.getId());
        if (existingSchema == null) {
            throw new ValidationException("Database schema not found with ID: " + schema.getId());
        }
        
        // Business validation for schema update
        boolean isSchemaNameExists = this.isSchemaNameExists(organizationId, schema.getSchemaName(), schema.getId());
        boolean isServiceExists = this.isServiceExists(organizationId, schema.getForService(), schema.getId());
        databaseSchemaBusinessValidator.validateDatabaseSchemaUpdate(schema, isSchemaNameExists, isServiceExists);
                
        // Apply schema updation business logic
        DatabaseSchemaUtil.applySchemaBusinessLogic(organization, schema, activeProfile);

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
        DatabaseSchema schemaToRemove = DatabaseSchemaUtil.findSchemaById(organization, schemaId);
        if (schemaToRemove == null) {
            throw new ValidationException("Database schema not found with ID: " + schemaId);
        }
        
        // Business validation for schema removal
        databaseSchemaBusinessValidator.validateDatabaseSchemaRemoval(organization, schemaToRemove);
        
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
                .map(organization -> DatabaseSchemaUtil.findSchemaById(organization, schemaId))
                .filter(schema -> schema != null);
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

    @Override
    public boolean isServiceExists(String organizationId, String forService, String excludeSchemaId) {
        if (!StringUtils.hasText(forService)) {
            return false;
        }
        
        List<DatabaseSchema> schemas = getDatabaseSchemas(organizationId);
        
        return schemas.stream()
                .filter(schema -> StringUtils.hasText(excludeSchemaId) ? 
                        !schema.getId().equals(excludeSchemaId) : true)
                .anyMatch(schema -> forService.equalsIgnoreCase(schema.getForService()));
    }

    // ========== PRIVATE HELPER METHODS ==========

    private Optional<DatabaseConfig> getDatabaseConfig(String organizationId) {
        return getOrgById(organizationId)
                .map(Organization::getDatabase)
                .filter(database -> database != null);
    }

}