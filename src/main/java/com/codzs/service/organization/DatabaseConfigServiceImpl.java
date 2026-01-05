package com.codzs.service.organization;

import com.codzs.entity.organization.DatabaseConfig;
import com.codzs.entity.organization.Organization;
import com.codzs.repository.organization.DatabaseConfigRepository;
import com.codzs.repository.organization.OrganizationRepository;
import com.codzs.validation.organization.DatabaseConfigBusinessValidator;
import com.codzs.framework.util.database.DatabaseConnectionTestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * Service implementation for DatabaseConfig-related business operations.
 * Manages database configuration operations for organizations
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
    public boolean testDatabaseConnection(String organizationId) {
        log.debug("Testing database connection for organization ID: {}", organizationId);
        
        return getDatabaseConfig(organizationId)
            .map(databaseConfig -> {
                boolean result = false;
                try {
                    // Test connection using the utility - auto-detects certificate usage
                    result = 
                        DatabaseConnectionTestUtil.testConnection(
                            databaseConfig.getConnectionString(), 
                            databaseConfig.getCertificate()
                        );
                    
                    if (result) {
                        log.info("Database connection test successful for organization: {}", organizationId);
                    }
                } catch (Exception e) {
                        log.warn("Database connection test exception for organization {}: {}", 
                            organizationId, e.getMessage());
                        return false;
                }
                
                return result;
            }
        )
        .orElseGet(() -> {
            log.warn("No database configuration found for organization ID: {}", organizationId);
            return false;
        });
    }

    // ========== PRIVATE HELPER METHODS ==========

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