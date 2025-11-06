package com.codzs.service.organization;

import com.codzs.entity.organization.DatabaseConfig;
import com.codzs.entity.organization.DatabaseSchema;
import com.codzs.entity.organization.Organization;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for DatabaseConfig-related business operations.
 * Manages database configuration and schema operations for organizations
 * with proper business validation and transaction management.
 * 
 * @author Codzs Team
 * @since 1.0
 */
public interface DatabaseConfigService {

    // ========== API FLOW METHODS ==========

    /**
     * Updates database configuration for an organization.
     * API: PUT /api/v1/organizations/{id}/database
     *
     * @param organizationId the organization ID
     * @param connectionString the database connection string
     * @param certificate the database certificate
     * @return the updated organization entity
     */
    Organization updateDatabaseConfig(String organizationId, String connectionString, String certificate);

    /**
     * Gets database configuration for an organization.
     * API: GET /api/v1/organizations/{id}/database
     *
     * @param organizationId the organization ID
     * @return Optional containing the database configuration entity, or empty if not found
     */
    Optional<DatabaseConfig> getDatabaseConfig(String organizationId);

    /**
     * Adds a new database schema to an organization.
     * API: POST /api/v1/organizations/{id}/database/schemas
     *
     * @param organizationId the organization ID
     * @param schema the database schema entity to add
     * @return list of all database schemas after addition
     */
    List<DatabaseSchema> addDatabaseSchema(String organizationId, DatabaseSchema schema);

    /**
     * Updates an existing database schema for an organization.
     * API: PUT /api/v1/organizations/{id}/database/schemas/{schemaId}
     *
     * @param organizationId the organization ID
     * @param schema the database schema entity with updates
     * @return the updated organization entity
     */
    Organization updateDatabaseSchema(String organizationId, DatabaseSchema schema);

    /**
     * Removes a database schema from an organization.
     * API: DELETE /api/v1/organizations/{id}/database/schemas/{schemaId}
     *
     * @param organizationId the organization ID
     * @param schemaId the schema ID to remove
     * @return the updated organization entity without the schema
     */
    Organization removeDatabaseSchema(String organizationId, String schemaId);

    /**
     * Gets all database schemas for an organization.
     * API: GET /api/v1/organizations/{id}/database/schemas
     *
     * @param organizationId the organization ID
     * @return list of all database schema entities
     */
    List<DatabaseSchema> getDatabaseSchemas(String organizationId);

    /**
     * Lists database schemas for an organization with optional filtering.
     * API: GET /api/v1/organizations/{id}/database/schemas
     *
     * @param organizationId the organization ID
     * @param forService filter by service type (optional)
     * @param status filter by schema status (optional)
     * @param headerOrganizationId organization context header (optional)
     * @param tenantId tenant context header (optional)
     * @return list of database schema entities (filtered if parameters provided)
     */
    List<DatabaseSchema> listDatabaseSchemas(String organizationId, String forService, String status);

    /**
     * Gets a specific database schema for an organization.
     * API: GET /api/v1/organizations/{id}/database/schemas/{schemaId}
     *
     * @param organizationId the organization ID
     * @param schemaId the schema ID
     * @return Optional containing the database schema entity, or empty if not found
     */
    Optional<DatabaseSchema> getDatabaseSchema(String organizationId, String schemaId);

    /**
     * Tests database connectivity for an organization.
     * API: POST /api/v1/organizations/{id}/database/test-connection
     *
     * @param organizationId the organization ID
     * @return true if connection is successful
     */
    boolean testDatabaseConnection(String organizationId);

    // ========== UTILITY METHODS ==========

    /**
     * Generates default schema name for a service.
     *
     * @param organizationAbbr the organization abbreviation
     * @param serviceType the service type
     * @param environment the environment (optional)
     * @return generated schema name
     */
    String generateSchemaName(String organizationAbbr, String serviceType, String environment);

    /**
     * Checks if schema name already exists for an organization.
     *
     * @param organizationId the organization ID
     * @param schemaName the schema name
     * @param excludeSchemaId ID to exclude from check (for updates)
     * @return true if schema name exists
     */
    boolean isSchemaNameExists(String organizationId, String schemaName, String excludeSchemaId);
}