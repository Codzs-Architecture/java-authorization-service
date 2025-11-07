package com.codzs.service.organization;

import com.codzs.entity.organization.DatabaseConfig;
import com.codzs.entity.organization.Organization;

import java.util.Optional;

/**
 * Service interface for DatabaseConfig-related business operations.
 * Manages database configuration operations for organizations
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
     * Tests database connectivity for an organization.
     * API: POST /api/v1/organizations/{id}/database/test-connection
     *
     * @param organizationId the organization ID
     * @return true if connection is successful
     */
    boolean testDatabaseConnection(String organizationId);
}