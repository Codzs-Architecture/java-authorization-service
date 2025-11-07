package com.codzs.service.plan;

import com.codzs.constant.plan.PlanConstants;
import com.codzs.entity.plan.Plan;
import com.codzs.framework.constant.CommonConstants;
import com.codzs.repository.plan.PlanRepository;
import com.codzs.validation.plan.PlanBusinessValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for Plan-related business operations.
 * Provides entry point methods for plan API endpoints
 * with proper business validation and transaction management.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;
    private final PlanBusinessValidator planBusinessValidator;

    @Autowired
    public PlanServiceImpl(PlanRepository planRepository,
                          PlanBusinessValidator planBusinessValidator) {
        this.planRepository = planRepository;
        this.planBusinessValidator = planBusinessValidator;
    }

    // ========== API FLOW METHODS ==========

    @Override
    @Transactional
    public Plan createPlan(Plan plan) {
        log.debug("Creating plan with name: {}", plan.getName());
        
        // Business validation as first step
        planBusinessValidator.validatePlanCreationFlow(plan);
        
        // Business logic for creation
        applyCreationBusinessLogic(plan);
        
        // Save plan
        Plan savedPlan = planRepository.save(plan);
        
        log.info("Created plan with ID: {} and name: {}", 
                savedPlan.getId(), savedPlan.getName());
        
        return savedPlan;
    }

    @Override
    @Transactional
    public Plan updatePlan(Plan plan) {
        log.debug("Updating plan ID: {}", plan.getId());
        
        // Business validation as first step
        planBusinessValidator.validatePlanUpdateFlow(plan);
        
        // Apply update business logic
        applyUpdateBusinessLogic(plan);
        
        // Save updated plan
        Plan updatedPlan = planRepository.save(plan);
        
        log.info("Updated plan with ID: {}", updatedPlan.getId());
        
        return updatedPlan;
    }

    @Override
    public Plan getPlanById(String planId) {
        log.debug("Getting plan by ID: {}", planId);
        
        return planRepository.findByIdAndDeletedDateIsNull(planId)
                .orElse(null);
    }

    @Override
    public Page<Plan> listPlans(List<String> statuses, List<String> planTypes, String searchText, Pageable pageable) {
        log.debug("Listing plans with filters - statuses: {}, types: {}, search: {}", 
                statuses, planTypes, searchText);
        
        // Convert string statuses to boolean statuses
        List<Boolean> statusBooleans = convertToStatusBooleans(statuses);
        
        // Normalize search text
        String normalizedSearchText = StringUtils.hasText(searchText) ? searchText.trim() : "";
        
        return planRepository.findWithFilters(
                statusBooleans != null ? statusBooleans : new ArrayList<>(),
                planTypes != null ? planTypes : new ArrayList<>(),
                normalizedSearchText,
                pageable
        );
    }

    @Override
    @Transactional
    public Plan activatePlan(String planId) {
        log.debug("Activating plan ID: {}", planId);
        
        // Business validation as first step
        planBusinessValidator.validatePlanActivationFlow(planId);
        
        // Retrieve existing plan
        Plan plan = findById(planId);
        
        // Update status to active
        plan.setIsActive(true);
        
        // Apply activation business logic
        applyActivationBusinessLogic(plan);
        
        // Save activated plan
        Plan activatedPlan = planRepository.save(plan);
        
        log.info("Activated plan with ID: {}", activatedPlan.getId());
        
        return activatedPlan;
    }

    @Override
    @Transactional
    public Plan deactivatePlan(String planId) {
        log.debug("Deactivating plan ID: {}", planId);
        
        // Business validation as first step
        planBusinessValidator.validatePlanDeactivationFlow(planId);
        
        // Retrieve existing plan
        Plan plan = findById(planId);
        
        // Update status to inactive
        plan.setIsActive(false);
        
        // Apply deactivation business logic
        applyDeactivationBusinessLogic(plan);
        
        // Save deactivated plan
        Plan deactivatedPlan = planRepository.save(plan);
        
        log.info("Deactivated plan with ID: {}", deactivatedPlan.getId());
        
        return deactivatedPlan;
    }

    @Override
    @Transactional
    public Plan deprecatePlan(String planId) {
        log.debug("Deprecating plan ID: {}", planId);
        
        // Business validation as first step
        planBusinessValidator.validatePlanDeprecationFlow(planId);
        
        // Retrieve existing plan
        Plan plan = findById(planId);
        
        // Mark as deprecated
        plan.setIsDeprecated(true);
        
        // Apply deprecation business logic
        applyDeprecationBusinessLogic(plan);
        
        // Save deprecated plan
        Plan deprecatedPlan = planRepository.save(plan);
        
        log.info("Deprecated plan with ID: {}", deprecatedPlan.getId());
        
        return deprecatedPlan;
    }

    @Override
    public List<Plan> getPlansForAutocomplete(List<String> statuses, String searchQuery, Pageable pageable) {
        log.debug("Getting plans for autocomplete with query: {}", searchQuery);
        
        // Convert string statuses to boolean statuses
        List<Boolean> statusBooleans = convertToStatusBooleans(statuses);
        
        // Default to active plans if no statuses provided
        if (statusBooleans == null || statusBooleans.isEmpty()) {
            statusBooleans = Arrays.asList(Boolean.TRUE);
        }
        
        String normalizedQuery = StringUtils.hasText(searchQuery) ? searchQuery.trim() : "";
        
        return planRepository.findForAutocomplete(statusBooleans, normalizedQuery, pageable);
    }

    @Override
    @Transactional
    public Plan deletePlan(String planId, String deletedBy) {
        log.debug("Soft deleting plan ID: {} by user: {}", planId, deletedBy);
        
        // Retrieve existing plan
        Plan plan = findById(planId);
        if (plan == null) {
            log.warn("Plan not found for deletion: {}", planId);
            return null;
        }
        
        // Business validation for deletion
        validatePlanDeletion(plan);
        
        // Perform soft delete
        plan.softDelete(deletedBy);
        
        // Apply deletion business logic
        applyDeletionBusinessLogic(plan);
        
        // Save deleted plan
        Plan deletedPlan = planRepository.save(plan);
        
        log.info("Soft deleted plan with ID: {}", deletedPlan.getId());
        
        return deletedPlan;
    }

    // ========== UTILITY METHODS FOR BUSINESS VALIDATION ==========

    @Override
    public Plan findById(String planId) {
        return planRepository.findByIdAndDeletedDateIsNull(planId)
                .orElse(null);
    }

    @Override
    public boolean isNameAlreadyExists(String name, String excludeId) {
        if (!StringUtils.hasText(name)) {
            return false;
        }
        
        if (StringUtils.hasText(excludeId)) {
            // For updates - check if name exists excluding current plan
            return planRepository.findByNameAndDeletedDateIsNull(name)
                    .map(plan -> !plan.getId().equals(excludeId))
                    .orElse(false);
        } else {
            // For creation - check if name exists at all
            return planRepository.existsByNameAndDeletedDateIsNull(name);
        }
    }

    @Override
    public boolean isCodeAlreadyExists(String code, String excludeId) {
        // TODO: Implement when code field is added to Plan entity
        log.debug("Code uniqueness check not implemented yet for code: {}", code);
        return false;
    }

    @Override
    public boolean isPlanCompatibleWithOrganizationType(String planId, String organizationType) {
        Plan plan = findById(planId);
        if (plan == null || !StringUtils.hasText(organizationType)) {
            return false;
        }
        
        return isPlanTypeCompatibleWithOrganizationType(plan.getType(), organizationType);
    }

    @Override
    public boolean isPlanCompatibleWithOrganizationSize(String planId, String organizationSize) {
        Plan plan = findById(planId);
        if (plan == null || !StringUtils.hasText(organizationSize)) {
            return false;
        }
        
        return isPlanTypeCompatibleWithOrganizationSize(plan.getType(), organizationSize);
    }

    @Override
    public boolean isPlanAvailableInRegion(String planId, String region) {
        Plan plan = findById(planId);
        if (plan == null || !StringUtils.hasText(region)) {
            return false;
        }
        
        // Check if region is in default available regions
        return Arrays.asList(PlanConstants.DEFAULT_AVAILABLE_REGIONS).contains(region);
    }

    @Override
    public boolean isTransitionAllowed(String fromPlanType, String toPlanType) {
        // TODO: Implement plan transition rules when PlanTypeEnum is available
        // For now, allow all transitions except downgrades
        log.debug("Plan transition validation not fully implemented yet");
        return true;
    }

    @Override
    public boolean hasPlanCapacity(String planId) {
        Plan plan = findById(planId);
        if (plan == null) {
            return false;
        }
        
        // Check current subscription count against plan capacity
        long currentSubscriptions = planRepository.countActiveSubscriptionsByPlanId(planId);
        return currentSubscriptions < PlanConstants.MAX_ORGANIZATIONS_PER_PLAN;
    }

    @Override
    public int comparePlanLevels(String parentPlanId, String childPlanId) {
        Plan parentPlan = findById(parentPlanId);
        Plan childPlan = findById(childPlanId);
        
        if (parentPlan == null || childPlan == null) {
            return 0;
        }
        
        int parentLevel = getPlanLevel(parentPlan.getType());
        int childLevel = getPlanLevel(childPlan.getType());
        
        return parentLevel - childLevel;
    }

    @Override
    public boolean hasActiveSubscriptions(String planId) {
        // TODO: Implement subscription check when subscription service is available
        long subscriptionCount = planRepository.countActiveSubscriptionsByPlanId(planId);
        return subscriptionCount > 0;
    }

    @Override
    public List<Plan> getPlansByType(String planType) {
        return planRepository.findByTypeAndDeletedDateIsNull(planType);
    }

    @Override
    public List<Plan> getActivePlans() {
        return planRepository.findByIsActiveTrueAndDeletedDateIsNull();
    }

    // ========== PRIVATE HELPER METHODS ==========

    private void applyCreationBusinessLogic(Plan plan) {
        // Set creation defaults
        if (plan.getIsActive() == null) {
            plan.setIsActive(false); // Plans start inactive by default
        }
        
        if (plan.getIsDeprecated() == null) {
            plan.setIsDeprecated(false);
        }
        
        log.debug("Applied creation business logic for plan: {}", plan.getName());
    }

    private void applyUpdateBusinessLogic(Plan plan) {
        // Apply any additional update business logic here
        log.debug("Applied update business logic for plan: {}", plan.getName());
    }

    private void applyActivationBusinessLogic(Plan plan) {
        // Apply any additional activation business logic here
        log.debug("Applied activation business logic for plan: {}", plan.getName());
    }

    private void applyDeactivationBusinessLogic(Plan plan) {
        // Apply any additional deactivation business logic here
        log.debug("Applied deactivation business logic for plan: {}", plan.getName());
    }

    private void applyDeprecationBusinessLogic(Plan plan) {
        // When deprecating, also deactivate the plan
        plan.setIsActive(false);
        
        log.debug("Applied deprecation business logic for plan: {}", plan.getName());
    }

    private void validatePlanDeletion(Plan plan) {
        // Check if plan has active subscriptions
        if (hasActiveSubscriptions(plan.getId())) {
            throw new IllegalStateException("Cannot delete plan with active subscriptions");
        }
        
        log.debug("Validated plan deletion for plan: {}", plan.getName());
    }

    private void applyDeletionBusinessLogic(Plan plan) {
        // Apply any additional deletion business logic here
        log.debug("Applied deletion business logic for plan: {}", plan.getName());
    }

    private List<Boolean> convertToStatusBooleans(List<String> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return null;
        }
        
        return statuses.stream()
                .filter(StringUtils::hasText)
                .map(status -> {
                    try {
                        return CommonConstants.ACTIVE.equalsIgnoreCase(status.trim()) || 
                               "TRUE".equalsIgnoreCase(status.trim());
                    } catch (Exception e) {
                        log.warn("Invalid plan status: {}", status);
                        return null;
                    }
                })
                .filter(status -> status != null)
                .collect(Collectors.toList());
    }

    private boolean isPlanTypeCompatibleWithOrganizationType(String planType, String organizationType) {
        return switch (planType) {
            case PlanConstants.PLAN_TYPE_BASIC -> 
                Arrays.asList(PlanConstants.BASIC_PLAN_COMPATIBLE_ORG_TYPES).contains(organizationType);
            case PlanConstants.PLAN_TYPE_STANDARD -> 
                Arrays.asList(PlanConstants.STANDARD_PLAN_COMPATIBLE_ORG_TYPES).contains(organizationType);
            case PlanConstants.PLAN_TYPE_PREMIUM -> 
                Arrays.asList(PlanConstants.PREMIUM_PLAN_COMPATIBLE_ORG_TYPES).contains(organizationType);
            case PlanConstants.PLAN_TYPE_ENTERPRISE -> 
                Arrays.asList(PlanConstants.ENTERPRISE_PLAN_COMPATIBLE_ORG_TYPES).contains(organizationType);
            case PlanConstants.PLAN_TYPE_CUSTOM -> true; // Custom plans are compatible with all org types
            default -> false;
        };
    }

    private boolean isPlanTypeCompatibleWithOrganizationSize(String planType, String organizationSize) {
        return switch (planType) {
            case PlanConstants.PLAN_TYPE_BASIC -> 
                Arrays.asList(PlanConstants.BASIC_PLAN_COMPATIBLE_ORG_SIZES).contains(organizationSize);
            case PlanConstants.PLAN_TYPE_STANDARD -> 
                Arrays.asList(PlanConstants.STANDARD_PLAN_COMPATIBLE_ORG_SIZES).contains(organizationSize);
            case PlanConstants.PLAN_TYPE_PREMIUM -> 
                Arrays.asList(PlanConstants.PREMIUM_PLAN_COMPATIBLE_ORG_SIZES).contains(organizationSize);
            case PlanConstants.PLAN_TYPE_ENTERPRISE -> 
                Arrays.asList(PlanConstants.ENTERPRISE_PLAN_COMPATIBLE_ORG_SIZES).contains(organizationSize);
            case PlanConstants.PLAN_TYPE_CUSTOM -> true; // Custom plans are compatible with all org sizes
            default -> false;
        };
    }

    private int getPlanLevel(String planType) {
        return switch (planType) {
            case PlanConstants.PLAN_TYPE_BASIC -> PlanConstants.PLAN_LEVEL_BASIC;
            case PlanConstants.PLAN_TYPE_STANDARD -> PlanConstants.PLAN_LEVEL_STANDARD;
            case PlanConstants.PLAN_TYPE_PREMIUM -> PlanConstants.PLAN_LEVEL_PREMIUM;
            case PlanConstants.PLAN_TYPE_ENTERPRISE -> PlanConstants.PLAN_LEVEL_ENTERPRISE;
            case PlanConstants.PLAN_TYPE_CUSTOM -> PlanConstants.PLAN_LEVEL_CUSTOM;
            default -> 0;
        };
    }
}