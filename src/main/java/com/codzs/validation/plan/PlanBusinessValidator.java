package com.codzs.validation.plan;

import com.codzs.constant.plan.PlanConstants;
import com.codzs.entity.plan.Plan;
import com.codzs.exception.type.validation.ValidationException;
import com.codzs.repository.plan.PlanRepository;
import com.codzs.service.subscription.SubscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Business validator for Plan-related operations.
 * Provides entry point methods for plan API endpoints
 * and handles plan-specific business validation logic.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Component
@Slf4j
public class PlanBusinessValidator {
    
    private final PlanRepository planRepository;
    private final SubscriptionService subscriptionService;

    @Autowired
    public PlanBusinessValidator(PlanRepository planRepository,
                               SubscriptionService subscriptionService) {
        this.planRepository = planRepository;
        this.subscriptionService = subscriptionService;
    }

    // ========== ENTRY POINT METHODS FOR PLAN APIs ==========

    /**
     * Entry point for plan creation business validation.
     * API: POST /api/v1/plans
     *
     * @param plan the plan entity
     * @throws ValidationException if business validation fails
     */
    public void validatePlanCreationFlow(Plan plan) throws ValidationException {
        List<ValidationException.ValidationError> errors = new ArrayList<>();

        validatePlanUniqueness(plan.getName(), null, errors);
        validatePlanBusinessRules(plan, errors);
        validatePlanCapacityLimits(plan, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException("Plan creation business validation failed", errors);
        }
    }

    /**
     * Entry point for plan update business validation.
     * API: PUT /api/v1/plans/{id}
     *
     * @param plan the plan entity with updates
     * @throws ValidationException if business validation fails
     */
    public void validatePlanUpdateFlow(Plan plan) throws ValidationException {
        List<ValidationException.ValidationError> errors = new ArrayList<>();

        Plan existingPlan = validatePlanExistsAndUpdatable(plan.getId(), errors);
        if (existingPlan == null) {
            throw new ValidationException("Plan update business validation failed", errors);
        }

        if (hasFieldChanged(plan.getName(), existingPlan.getName())) {
            validatePlanUniqueness(plan.getName(), plan.getId(), errors);
        }

        validateUpdateBusinessConstraints(existingPlan, plan, errors);
        validatePlanBusinessRules(plan, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException("Plan update business validation failed", errors);
        }
    }

    /**
     * Entry point for plan activation business validation.
     * API: PUT /api/v1/plans/{id}/activate
     *
     * @param planId the plan ID to activate
     * @throws ValidationException if business validation fails
     */
    public void validatePlanActivationFlow(String planId) throws ValidationException {
        List<ValidationException.ValidationError> errors = new ArrayList<>();

        Plan plan = validatePlanExists(planId, errors);
        if (plan == null) {
            throw new ValidationException("Plan activation business validation failed", errors);
        }

        validateActivationBusinessRules(plan, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException("Plan activation business validation failed", errors);
        }
    }

    /**
     * Entry point for plan deactivation business validation.
     * API: PUT /api/v1/plans/{id}/deactivate
     *
     * @param planId the plan ID to deactivate
     * @throws ValidationException if business validation fails
     */
    public void validatePlanDeactivationFlow(String planId) throws ValidationException {
        List<ValidationException.ValidationError> errors = new ArrayList<>();

        Plan plan = validatePlanExists(planId, errors);
        if (plan == null) {
            throw new ValidationException("Plan deactivation business validation failed", errors);
        }

        validateDeactivationBusinessRules(plan, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException("Plan deactivation business validation failed", errors);
        }
    }

    /**
     * Entry point for plan deprecation business validation.
     * API: PUT /api/v1/plans/{id}/deprecate
     *
     * @param planId the plan ID to deprecate
     * @throws ValidationException if business validation fails
     */
    public void validatePlanDeprecationFlow(String planId) throws ValidationException {
        List<ValidationException.ValidationError> errors = new ArrayList<>();

        Plan plan = validatePlanExists(planId, errors);
        if (plan == null) {
            throw new ValidationException("Plan deprecation business validation failed", errors);
        }

        validateDeprecationBusinessRules(plan, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException("Plan deprecation business validation failed", errors);
        }
    }

    // ========== CORE VALIDATION METHODS ==========

    private void validatePlanUniqueness(String name, String excludeId, 
                                       List<ValidationException.ValidationError> errors) {
        if (StringUtils.hasText(name)) {
            validatePlanNameUniqueness(name, excludeId, errors);
        }
    }

    private void validatePlanBusinessRules(Plan plan, 
                                         List<ValidationException.ValidationError> errors) {
        // Validate plan name format
        if (StringUtils.hasText(plan.getName()) && 
            !plan.getName().matches(PlanConstants.PLAN_NAME_PATTERN)) {
            errors.add(new ValidationException.ValidationError("name", 
                PlanConstants.PLAN_NAME_PATTERN_MESSAGE));
        }

        // Validate plan type
        validatePlanType(plan.getType(), errors);

        // Validate validity period
        validateValidityPeriod(plan.getValidityPeriod(), plan.getValidityPeriodUnit(), errors);

        // Validate pricing
        validatePricing(plan, errors);

        // Validate user limits
        validateUserLimit(plan, errors);

        // Validate tenant limits
        validateTenantLimits(plan, errors);

        // Validate storage limits
        validateStorageLimit(plan, errors);
    }

    private void validatePlanCapacityLimits(Plan plan, 
                                          List<ValidationException.ValidationError> errors) {
        // Validate that we don't exceed maximum number of active plans
        if (plan.getIsActive() != null && plan.getIsActive()) {
            List<Plan> activePlans = planRepository.findByIsActiveTrueAndDeletedDateIsNull();
            if (activePlans.size() >= PlanConstants.MAX_ACTIVE_PLANS) {
                errors.add(new ValidationException.ValidationError("isActive", 
                    "Maximum number of active plans (" + PlanConstants.MAX_ACTIVE_PLANS + ") reached"));
            }
        }
    }

    private void validatePlanType(String planType, List<ValidationException.ValidationError> errors) {
        // Plan type required validation is handled by @NotBlank annotation in Plan entity
        // Plan type enum validation should use dynamic enum from configuration
        
        String[] validTypes = {
            PlanConstants.PLAN_TYPE_BASIC,
            PlanConstants.PLAN_TYPE_STANDARD,
            PlanConstants.PLAN_TYPE_PREMIUM,
            PlanConstants.PLAN_TYPE_ENTERPRISE,
            PlanConstants.PLAN_TYPE_CUSTOM
        };

        if (StringUtils.hasText(planType) && !Arrays.asList(validTypes).contains(planType)) {
            errors.add(new ValidationException.ValidationError("type", "Invalid plan type"));
        }
    }

    private void validateValidityPeriod(Integer validityPeriod, String validityPeriodUnit, 
                                      List<ValidationException.ValidationError> errors) {
        // Validity period required and positive validation is handled by @NotNull and @Min annotations in Plan entity
        // Validity period unit required validation is handled by @NotBlank annotation in Plan entity
        
        if (validityPeriod == null || !StringUtils.hasText(validityPeriodUnit)) {
            return; // Basic validations are handled by entity annotations
        }

        String[] validUnits = {
            PlanConstants.VALIDITY_UNIT_DAYS,
            PlanConstants.VALIDITY_UNIT_MONTHS,
            PlanConstants.VALIDITY_UNIT_YEARS
        };

        if (!Arrays.asList(validUnits).contains(validityPeriodUnit)) {
            errors.add(new ValidationException.ValidationError("validityPeriodUnit", 
                "Invalid validity period unit"));
            return;
        }

        // Convert to days for validation
        int totalDays = calculateTotalDays(validityPeriod, validityPeriodUnit);
        if (totalDays < PlanConstants.MIN_VALIDITY_PERIOD_DAYS) {
            errors.add(new ValidationException.ValidationError("validityPeriod", 
                "Validity period must be at least " + PlanConstants.MIN_VALIDITY_PERIOD_DAYS + " days"));
        }

        if (totalDays > PlanConstants.MAX_VALIDITY_PERIOD_DAYS) {
            errors.add(new ValidationException.ValidationError("validityPeriod", 
                "Validity period cannot exceed " + PlanConstants.MAX_VALIDITY_PERIOD_DAYS + " days"));
        }
    }

    private void validateUpdateBusinessConstraints(Plan existingPlan, Plan updatedPlan, 
                                                 List<ValidationException.ValidationError> errors) {
        // Check if plan has active subscriptions before allowing certain changes
        if (hasActiveSubscriptions(existingPlan.getId())) {
            if (hasFieldChanged(updatedPlan.getType(), existingPlan.getType())) {
                errors.add(new ValidationException.ValidationError("type", 
                    "Cannot change plan type while active subscriptions exist"));
            }

            if (hasFieldChanged(updatedPlan.getValidityPeriod(), existingPlan.getValidityPeriod()) ||
                hasFieldChanged(updatedPlan.getValidityPeriodUnit(), existingPlan.getValidityPeriodUnit())) {
                errors.add(new ValidationException.ValidationError("validityPeriod", 
                    "Cannot change validity period while active subscriptions exist"));
            }
        }

        // Validate downgrade restrictions
        if (isDowngrade(existingPlan, updatedPlan)) {
            validateDowngradeConstraints(existingPlan, updatedPlan, errors);
        }
    }

    private void validateActivationBusinessRules(Plan plan, 
                                                List<ValidationException.ValidationError> errors) {
        // Skip validation if plan is already active (idempotent operation)
        if (plan.getIsActive()) {
            return;
        }

        if (plan.getIsDeprecated() != null && plan.getIsDeprecated()) {
            errors.add(new ValidationException.ValidationError("isDeprecated", 
                "Cannot activate deprecated plan"));
        }

        // Check capacity limits
        List<Plan> activePlans = planRepository.findByIsActiveTrueAndDeletedDateIsNull();
        if (activePlans.size() >= PlanConstants.MAX_ACTIVE_PLANS) {
            errors.add(new ValidationException.ValidationError("isActive", 
                "Maximum number of active plans (" + PlanConstants.MAX_ACTIVE_PLANS + ") reached"));
        }
    }

    private void validateDeactivationBusinessRules(Plan plan, 
                                                  List<ValidationException.ValidationError> errors) {
        // Skip validation if plan is already inactive (idempotent operation)
        if (!plan.getIsActive()) {
            return;
        }

        if (hasActiveSubscriptions(plan.getId())) {
            errors.add(new ValidationException.ValidationError("activeSubscriptions", 
                "Cannot deactivate plan with active subscriptions"));
        }
    }

    private void validateDeprecationBusinessRules(Plan plan, 
                                                 List<ValidationException.ValidationError> errors) {
        // Skip validation if plan is already deprecated (idempotent operation)
        if (plan.getIsDeprecated() != null && plan.getIsDeprecated()) {
            return;
        }
    }

    private void validateDowngradeConstraints(Plan existingPlan, Plan updatedPlan, 
                                            List<ValidationException.ValidationError> errors) {
        // Validate user limit reduction
        if (updatedPlan.getMaxUsers() != null && existingPlan.getMaxUsers() != null &&
            updatedPlan.getMaxUsers() < existingPlan.getMaxUsers()) {
            errors.add(new ValidationException.ValidationError("maxUsers", 
                "Cannot reduce user limit below current limit while subscriptions exist"));
        }
        
        // Validate tenant limit reduction
        if (updatedPlan.getMaxTenants() != null && existingPlan.getMaxTenants() != null &&
            updatedPlan.getMaxTenants() < existingPlan.getMaxTenants()) {
            errors.add(new ValidationException.ValidationError("maxTenants", 
                "Cannot reduce tenant limit below current limit while subscriptions exist"));
        }
        
        // Validate storage limit reduction
        if (updatedPlan.getStorageLimit() != null && existingPlan.getStorageLimit() != null &&
            updatedPlan.getStorageLimit() < existingPlan.getStorageLimit()) {
            errors.add(new ValidationException.ValidationError("storageLimit", 
                "Cannot reduce storage limit below current limit while subscriptions exist"));
        }
    }

    // ========== HELPER METHODS ==========

    private void validateStorageLimit(Plan plan, List<ValidationException.ValidationError> errors) {
        if (plan.getStorageLimit() != null && 
            plan.getStorageLimit() > PlanConstants.MAX_PLAN_STORAGE_LIMIT_GB * 1024 * 1024 * 1024) {
            errors.add(new ValidationException.ValidationError("storageLimit", 
                "Plan storage limit cannot exceed " + PlanConstants.MAX_PLAN_STORAGE_LIMIT_GB + " GB"));
        }
    }

    private void validateTenantLimits(Plan plan, List<ValidationException.ValidationError> errors) {
        if (plan.getMaxTenants() != null && plan.getMaxTenants() > PlanConstants.MAX_PLAN_TENANT_LIMIT) {
            errors.add(new ValidationException.ValidationError("maxTenants", 
                "Plan tenant limit cannot exceed " + PlanConstants.MAX_PLAN_TENANT_LIMIT));
        }
    }

    private void validateUserLimit(Plan plan, List<ValidationException.ValidationError> errors) {
        if (plan.getMaxUsers() != null && plan.getMaxUsers() > PlanConstants.MAX_PLAN_USER_LIMIT) {
            errors.add(new ValidationException.ValidationError("maxUsers", 
                "Plan user limit cannot exceed " + PlanConstants.MAX_PLAN_USER_LIMIT));
        }
    }

    private void validatePricing(Plan plan, List<ValidationException.ValidationError> errors) {
        if (plan.getPrice() != null && plan.getPrice() > PlanConstants.MAX_PLAN_PRICE) {
            errors.add(new ValidationException.ValidationError("price", 
                "Plan price cannot exceed " + PlanConstants.MAX_PLAN_PRICE));
        }
    }

    private void validatePlanNameUniqueness(String name, String excludeId, 
                                           List<ValidationException.ValidationError> errors) {
        if (isNameAlreadyExists(name, excludeId)) {
            errors.add(new ValidationException.ValidationError("name", "Plan name already exists"));
        }
    }

    private Plan validatePlanExists(String planId, 
                                   List<ValidationException.ValidationError> errors) {
        // Plan ID required validation should be handled by request DTO annotations
        
        Plan plan = planRepository.findByIdAndDeletedDateIsNull(planId).orElse(null);
        if (plan == null) {
            errors.add(new ValidationException.ValidationError("planId", "Plan not found"));
            return null;
        }
        
        return plan;
    }

    private Plan validatePlanExistsAndUpdatable(String planId, 
                                               List<ValidationException.ValidationError> errors) {
        Plan plan = validatePlanExists(planId, errors);
        if (plan == null) {
            return null;
        }
        
        if (plan.getDeletedDate() != null) {
            errors.add(new ValidationException.ValidationError("planId", 
                "Cannot update deleted plan"));
            return null;
        }
        
        return plan;
    }

    private boolean hasFieldChanged(Object newValue, Object existingValue) {
        if (newValue == null) {
            return false;
        }
        return !newValue.equals(existingValue);
    }

    private int calculateTotalDays(Integer validityPeriod, String validityPeriodUnit) {
        return switch (validityPeriodUnit) {
            case PlanConstants.VALIDITY_UNIT_DAYS -> validityPeriod;
            case PlanConstants.VALIDITY_UNIT_MONTHS -> validityPeriod * 30;
            case PlanConstants.VALIDITY_UNIT_YEARS -> validityPeriod * 365;
            default -> 0;
        };
    }

    private boolean isDowngrade(Plan existingPlan, Plan updatedPlan) {
        // Compare plan hierarchy levels to determine if this is a downgrade
        int existingLevel = getPlanLevel(existingPlan.getType());
        int updatedLevel = getPlanLevel(updatedPlan.getType());
        
        return updatedLevel < existingLevel;
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

    private boolean isNameAlreadyExists(String name, String excludeId) {
        if (!StringUtils.hasText(name)) {
            return false;
        }
        
        if (StringUtils.hasText(excludeId)) {
            // For updates - check if name exists excluding current plan
            return planRepository.findByNameAndDeletedDateIsNull(name)
                    .map(plan -> !plan.getId().equals(excludeId))
                    .orElse(false);
        }
        
        // For creation - check if name exists at all
        return planRepository.existsByNameAndDeletedDateIsNull(name);
    }

    private boolean hasActiveSubscriptions(String planId) {
        // Use service layer to check for active subscriptions
        return subscriptionService.hasActiveSubscriptions(planId);
    }
}