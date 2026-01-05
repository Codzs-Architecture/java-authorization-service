package com.codzs.repository.plan;

import com.codzs.entity.plan.Plan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * MongoDB Repository for Plan entity operations.
 * Provides CRUD operations and custom queries for plan management.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Repository
public interface PlanRepository extends MongoRepository<Plan, String> {

    // ========== FIND BY UNIQUE FIELDS ==========

    /**
     * Finds an active plan by ID (not soft deleted).
     *
     * @param id the plan ID
     * @return optional plan entity
     */
    Optional<Plan> findByIdAndDeletedDateIsNull(String id);

    /**
     * Finds a plan by name (not soft deleted).
     *
     * @param name the plan name
     * @return optional plan entity
     */
    Optional<Plan> findByNameAndDeletedDateIsNull(String name);

    /**
     * Checks if a plan with given name exists (not soft deleted).
     *
     * @param name the plan name
     * @return true if exists, false otherwise
     */
    boolean existsByNameAndDeletedDateIsNull(String name);

    // ========== FIND BY STATUS ==========

    /**
     * Finds all active plans (not soft deleted).
     *
     * @return list of active plans
     */
    List<Plan> findByIsActiveTrueAndDeletedDateIsNull();

    /**
     * Finds all plans by active status (not soft deleted).
     *
     * @param isActive the active status
     * @return list of plans with specified active status
     */
    List<Plan> findByIsActiveAndDeletedDateIsNull(Boolean isActive);

    // ========== FIND BY TYPE ==========

    /**
     * Finds plans by type (not soft deleted).
     *
     * @param type the plan type
     * @return list of plans with specified type
     */
    List<Plan> findByTypeAndDeletedDateIsNull(String type);

    /**
     * Finds active plans by type (not soft deleted).
     *
     * @param type the plan type
     * @return list of active plans with specified type
     */
    List<Plan> findByTypeAndIsActiveTrueAndDeletedDateIsNull(String type);

    // ========== COMPLEX QUERIES ==========

    /**
     * Finds plans with filters and pagination.
     *
     * @param isActiveList list of active status values
     * @param planTypes list of plan types
     * @param searchText search text for name or description
     * @param pageable pagination parameters
     * @return page of filtered plans
     */
    @Query("{ $and: [ " +
           "  { 'deletedDate': null }, " +
           "  { $or: [ " +
           "    { ?0: { $size: 0 } }, " +
           "    { 'isActive': { $in: ?0 } } " +
           "  ] }, " +
           "  { $or: [ " +
           "    { ?1: { $size: 0 } }, " +
           "    { 'type': { $in: ?1 } } " +
           "  ] }, " +
           "  { $or: [ " +
           "    { ?2: '' }, " +
           "    { ?2: null }, " +
           "    { 'name': { $regex: ?2, $options: 'i' } }, " +
           "    { 'description': { $regex: ?2, $options: 'i' } } " +
           "  ] } " +
           "] }")
    Page<Plan> findWithFilters(List<Boolean> isActiveList, List<String> planTypes, String searchText, Pageable pageable);

    /**
     * Finds plans for autocomplete with filters.
     *
     * @param isActiveList list of active status values
     * @param searchText search text for name
     * @param pageable pagination parameters
     * @return list of plans for autocomplete
     */
    @Query("{ $and: [ " +
           "  { 'deletedDate': null }, " +
           "  { $or: [ " +
           "    { ?0: { $size: 0 } }, " +
           "    { 'isActive': { $in: ?0 } } " +
           "  ] }, " +
           "  { $or: [ " +
           "    { ?1: '' }, " +
           "    { ?1: null }, " +
           "    { 'name': { $regex: ?1, $options: 'i' } } " +
           "  ] } " +
           "] }")
    List<Plan> findForAutocomplete(List<Boolean> isActiveList, String searchText, Pageable pageable);

    // ========== BUSINESS VALIDATION QUERIES ==========

    /**
     * Checks if plan has active subscriptions.
     * Note: This is a placeholder query. In a real implementation,
     * this would join with subscription/organization tables.
     *
     * @param planId the plan ID
     * @return count of active subscriptions (placeholder)
     */
    @Query("{ 'id': ?0, 'deletedDate': null }")
    long countActiveSubscriptionsByPlanId(String planId);

    /**
     * Finds plans by multiple IDs (not soft deleted).
     *
     * @param planIds list of plan IDs
     * @return list of plans
     */
    List<Plan> findByIdInAndDeletedDateIsNull(List<String> planIds);

    /**
     * Finds deprecated plans (not soft deleted).
     *
     * @return list of deprecated plans
     */
    List<Plan> findByIsDeprecatedTrueAndDeletedDateIsNull();

    /**
     * Finds non-deprecated active plans (not soft deleted).
     *
     * @return list of non-deprecated active plans
     */
    @Query("{ $and: [ " +
           "  { 'deletedDate': null }, " +
           "  { 'isActive': true }, " +
           "  { $or: [ " +
           "    { 'isDeprecated': { $exists: false } }, " +
           "    { 'isDeprecated': false }, " +
           "    { 'isDeprecated': null } " +
           "  ] } " +
           "] }")
    List<Plan> findActiveNonDeprecatedPlans();
}