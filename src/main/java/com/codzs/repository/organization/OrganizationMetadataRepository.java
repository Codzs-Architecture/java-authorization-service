package com.codzs.repository.organization;

import com.codzs.entity.organization.Organization;
import com.codzs.entity.organization.OrganizationMetadata;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Organization Metadata MongoDB operations.
 * Provides methods for managing metadata as embedded objects within organizations.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Repository
public interface OrganizationMetadataRepository extends MongoRepository<Organization, String> {

    // ========== METADATA OPERATIONS ==========

    /**
     * Updates the entire metadata object for an organization.
     * Uses MongoDB $set operator to update the metadata sub-object.
     */
    @Update("{ '$set': { 'metadata': ?1 } }")
    @Query("{ '_id': ?0 }")
    void updateOrganizationMetadata(String organizationId, OrganizationMetadata metadata);

    /**
     * Updates only the industry field in organization metadata.
     * Uses MongoDB $set operator to update the specific field.
     */
    @Update("{ '$set': { 'metadata.industry': ?1 } }")
    @Query("{ '_id': ?0 }")
    void updateIndustry(String organizationId, String industry);

    /**
     * Updates only the size field in organization metadata.
     * Uses MongoDB $set operator to update the specific field.
     */
    @Update("{ '$set': { 'metadata.size': ?1 } }")
    @Query("{ '_id': ?0 }")
    void updateSize(String organizationId, String size);

    // ========== QUERY OPERATIONS ==========

    /**
     * Checks if an organization has metadata set.
     * Used for validation during metadata operations.
     */
    @Query("{ '_id': ?0, 'metadata': { $exists: true, $ne: null } }")
    boolean hasMetadata(String organizationId);
}