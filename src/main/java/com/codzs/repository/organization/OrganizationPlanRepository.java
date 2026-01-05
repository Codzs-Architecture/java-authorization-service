package com.codzs.repository.organization;

import com.codzs.entity.organization.OrganizationPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for OrganizationPlan MongoDB documents.
 * Provides methods for managing organization plan associations with minimal, reusable functionality.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Repository
public interface OrganizationPlanRepository extends MongoRepository<OrganizationPlan, String> {

    // Current active plan queries
    Optional<OrganizationPlan> findByOrganizationIdAndIsActiveTrueAndDeletedDateIsNull(String organizationId);
    
    @Query("{ 'organizationId': ?0, 'isActive': true, 'deletedDate': null, " +
           "$or: [ " +
           "  { 'validTo': null }, " +
           "  { 'validTo': { $gte: ?1 } } " +
           "] }")
    Optional<OrganizationPlan> findCurrentValidPlan(String organizationId, Instant currentTime);

    // Plan history and listing
    List<OrganizationPlan> findByOrganizationIdAndDeletedDateIsNullOrderByCreatedDateDesc(String organizationId);
    
    Page<OrganizationPlan> findByOrganizationIdAndDeletedDateIsNullOrderByCreatedDateDesc(String organizationId, Pageable pageable);

    // Plan history with date filtering
    @Query("{ 'organizationId': ?0, 'deletedDate': null, " +
           "'createdDate': { $gte: ?1, $lte: ?2 } }")
    Page<OrganizationPlan> findByOrganizationIdWithDateRange(String organizationId, 
                                                            Instant startDate, 
                                                            Instant endDate, 
                                                            Pageable pageable);

    // Plan validation and conflict checks
    boolean existsByOrganizationIdAndPlanIdAndIsActiveTrueAndDeletedDateIsNull(String organizationId, String planId);
    
    @Query("{ 'organizationId': ?0, 'isActive': true, 'deletedDate': null, " +
           "'validFrom': { $lte: ?1 }, " +
           "$or: [ " +
           "  { 'validTo': null }, " +
           "  { 'validTo': { $gte: ?2 } } " +
           "] }")
    List<OrganizationPlan> findConflictingPlans(String organizationId, Instant newValidFrom, Instant newValidTo);

    // Deactivate previous plans (for plan transitions)
    @Query("{ 'organizationId': ?0, 'isActive': true, 'deletedDate': null }")
    List<OrganizationPlan> findActivePlansForOrganization(String organizationId);

    // Expired plans cleanup
    @Query("{ 'isActive': true, 'deletedDate': null, 'validTo': { $lt: ?0 } }")
    List<OrganizationPlan> findExpiredActivePlans(Instant currentTime);

    // Bulk operations for organization deletion
    List<OrganizationPlan> findByOrganizationIdAndDeletedDateIsNull(String organizationId);

    // Plan usage analytics
    long countByPlanIdAndIsActiveTrueAndDeletedDateIsNull(String planId);
    
    @Query("{ 'planId': ?0, 'isActive': true, 'deletedDate': null, " +
           "'validFrom': { $lte: ?1 }, " +
           "$or: [ " +
           "  { 'validTo': null }, " +
           "  { 'validTo': { $gte: ?1 } } " +
           "] }")
    long countActiveUsageForPlan(String planId, Instant currentTime);

    // Utility methods for plan management
    @Query("{ 'organizationId': ?0, 'deletedDate': null }")
    boolean existsByOrganizationId(String organizationId);
    
    boolean existsByOrganizationIdAndIsActiveTrue(String organizationId);
    
    Optional<OrganizationPlan> findTopByOrganizationIdAndDeletedDateIsNullOrderByCreatedDateDesc(String organizationId);
}