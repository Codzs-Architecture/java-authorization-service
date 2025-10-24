package com.codzs.repository.organization;

import com.codzs.entity.organization.DatabaseSchema;
import com.codzs.entity.organization.Organization;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.Update;

/**
 * Repository interface for DatabaseConfig MongoDB operations.
 * Provides methods for managing database configurations and schemas 
 * as embedded objects within organizations.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Repository
public interface DatabaseConfigRepository extends MongoRepository<Organization, String> {

    // ========== DATABASE CONFIG OPERATIONS ==========

    /**
     * Updates the database connection string for an organization.
     * Only updates the connectionString field, preserving other database config fields.
     */
    @Update("{ '$set': { 'database.connectionString': ?1 } }")
    @Query("{ '_id': ?0 }")
    void updateDatabaseConnectionString(String organizationId, String connectionString);
    
    /**
     * Updates the database certificate for an organization.
     * Only updates the certificate field, preserving other database config fields.
     */
    @Update("{ '$set': { 'database.certificate': ?1 } }")
    @Query("{ '_id': ?0 }")
    void updateDatabaseCertificate(String organizationId, String certificate);

    // ========== DATABASE SCHEMA OPERATIONS ==========
    
    /**
     * Adds a new database schema to an organization's database configuration.
     * Uses MongoDB $push operator to add the schema to the schemas array.
     */
    @Update("{ '$push': { 'database.schemas': ?1 } }")
    @Query("{ '_id': ?0 }")
    void addDatabaseSchema(String organizationId, DatabaseSchema schema);
    
    /**
     * Removes a database schema from an organization's database configuration.
     * Uses MongoDB $pull operator to remove the schema by ID from the schemas array.
     */
    @Update("{ '$pull': { 'database.schemas': { 'id': ?1 } } }")
    @Query("{ '_id': ?0 }")
    void removeDatabaseSchema(String organizationId, String schemaId);
    
    /**
     * Updates the schema name for a specific database schema.
     * Uses MongoDB positional operator ($) to update the specific schema in the array.
     */
    @Update("{ '$set': { 'database.schemas.$.schemaName': ?2 } }")
    @Query("{ '_id': ?0, 'database.schemas.id': ?1 }")
    void updateDatabaseSchemaName(String organizationId, String schemaId, String schemaName);
    
    /**
     * Updates the service type for a specific database schema.
     * Uses MongoDB positional operator ($) to update the specific schema in the array.
     */
    @Update("{ '$set': { 'database.schemas.$.forService': ?2 } }")
    @Query("{ '_id': ?0, 'database.schemas.id': ?1 }")
    void updateDatabaseSchemaService(String organizationId, String schemaId, String forService);
    
    /**
     * Updates the description for a specific database schema.
     * Uses MongoDB positional operator ($) to update the specific schema in the array.
     */
    @Update("{ '$set': { 'database.schemas.$.description': ?2 } }")
    @Query("{ '_id': ?0, 'database.schemas.id': ?1 }")
    void updateDatabaseSchemaDescription(String organizationId, String schemaId, String description);

    // ========== QUERY OPERATIONS ==========
    
    /**
     * Checks if a database schema name already exists within an organization.
     * Used for validation during schema creation and updates.
     */
    @Query("{ '_id': ?0, 'database.schemas.schemaName': ?1 }")
    boolean existsBySchemaName(String organizationId, String schemaName);

}