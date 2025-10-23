package com.codzs.repository.organization;

import com.codzs.entity.organization.Organization;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Organization database configuration operations.
 * Provides specialized methods for managing database configurations within organizations.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Repository
public interface OrganizationDatabaseRepository extends MongoRepository<Organization, String> {

    // Database configuration retrieval
    @Query(value = "{ '_id': ?0, 'deletedDate': null }", fields = "{ 'database': 1, 'abbr': 1 }")
    Optional<Organization> findDatabaseConfigById(String organizationId);

    // Connection string operations
    @Query("{ '_id': ?0, 'deletedDate': null }")
    @Update("{ '$set': { 'database.connectionString': ?1, 'lastModifiedDate': ?2, 'lastModifiedBy': ?3 } }")
    void updateConnectionString(String organizationId, String connectionString, Instant lastModifiedDate, String lastModifiedBy);

    // Certificate operations
    @Query("{ '_id': ?0, 'deletedDate': null }")
    @Update("{ '$set': { 'database.certificate': ?1, 'lastModifiedDate': ?2, 'lastModifiedBy': ?3 } }")
    void updateCertificate(String organizationId, String certificate, Instant lastModifiedDate, String lastModifiedBy);

    // Schema management operations
    @Query("{ '_id': ?0, 'deletedDate': null }")
    @Update("{ '$push': { 'database.schemas': ?1 }, '$set': { 'lastModifiedDate': ?2, 'lastModifiedBy': ?3 } }")
    void addSchemaToDatabase(String organizationId, Object schema, Instant lastModifiedDate, String lastModifiedBy);

    @Query("{ '_id': ?0, 'deletedDate': null, 'database.schemas.forService': ?1 }")
    @Update("{ '$set': { 'database.schemas.$.status': ?2, 'lastModifiedDate': ?3, 'lastModifiedBy': ?4 } }")
    void updateSchemaStatus(String organizationId, String forService, String status, Instant lastModifiedDate, String lastModifiedBy);

    @Query("{ '_id': ?0, 'deletedDate': null }")
    @Update("{ '$pull': { 'database.schemas': { 'forService': ?1 } }, '$set': { 'lastModifiedDate': ?2, 'lastModifiedBy': ?3 } }")
    void removeSchemaFromDatabase(String organizationId, String forService, Instant lastModifiedDate, String lastModifiedBy);

    // Schema existence and validation
    @Query("{ '_id': ?0, 'deletedDate': null, 'database.schemas.forService': ?1 }")
    Optional<Organization> findByIdAndSchemaService(String organizationId, String forService);

    @Query("{ '_id': ?0, 'deletedDate': null, 'database.schemas.schemaName': ?1 }")
    Optional<Organization> findByIdAndSchemaName(String organizationId, String schemaName);

    @Query("{ 'deletedDate': null, 'database.schemas.schemaName': ?0 }")
    Optional<Organization> findBySchemaName(String schemaName);

    @Query("{ 'deletedDate': null, 'database.schemas.schemaName': ?0 }")
    boolean existsBySchemaName(String schemaName);

    // Database configuration analytics
    @Query("{ 'deletedDate': null, 'database.schemas.forService': ?0 }")
    List<Organization> findByDatabaseService(String forService);

    @Query("{ 'deletedDate': null, 'database.schemas.status': ?0 }")
    List<Organization> findBySchemaStatus(String status);

    // Database health and monitoring
    @Query("{ 'deletedDate': null, 'database.schemas.status': 'active' }")
    List<Organization> findWithActiveSchemas();

    @Query("{ 'deletedDate': null, 'database.schemas.status': 'inactive' }")
    List<Organization> findWithInactiveSchemas();

    @Query("{ 'deletedDate': null, 'database.schemas.status': 'creating' }")
    List<Organization> findWithPendingSchemas();

    // Schema count and statistics
    @Query("{ 'deletedDate': null, 'database.schemas.forService': ?0 }")
    long countByDatabaseService(String forService);

    @Query("{ 'deletedDate': null, 'database.schemas.status': ?0 }")
    long countBySchemaStatus(String status);

    // Organizations without specific database services
    @Query("{ 'deletedDate': null, 'database.schemas.forService': { '$ne': ?0 } }")
    List<Organization> findWithoutDatabaseService(String forService);
}