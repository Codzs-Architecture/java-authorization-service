package com.codzs.validation.organization;

import com.codzs.constant.plan.PlanConstants;
import com.codzs.constant.plan.PlanTypeEnum;
import com.codzs.entity.organization.Organization;
import com.codzs.entity.organization.OrganizationPlan;
import com.codzs.entity.plan.Plan;
import com.codzs.exception.type.validation.ValidationException;
import com.codzs.service.organization.OrganizationPlanService;
import com.codzs.service.plan.PlanService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Business validator for Organization-Plan association operations.
 * Focuses on plan compatibility and association rules for organization APIs.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Component
public class OrganizationPlanBusinessValidator {
    
    private final PlanService planService;

    @Autowired
    public OrganizationPlanBusinessValidator(
        PlanService planService, 
        PlanTypeEnum planTypeEnum
    ) {
        this.planService = planService;
    }

    // ========== ENTRY POINT METHODS FOR ORGANIZATION APIs ==========

    /**
     * Basic plan association validation without usage data (for cross-validator calls).
     * This method only validates plan existence and basic compatibility rules.
     * API: POST /api/v1/organizations/{id}/plans
     *
     * @param organization the organization
     * @param organizationPlan the organization plan entity
     * @param errors list to collect validation errors
     */
    public void validatePlanAssociationForOrganization(Organization organization, OrganizationPlan organizationPlan,
                                                      List<ValidationException.ValidationError> errors) {
        Plan plan = validatePlanExistsAndAvailable(organizationPlan.getPlanId(), errors);
        if (plan == null) {
            return;
        }

        validatePlanCompatibilityWithOrganization(organization, plan, errors);
        // Note: Detailed validation with usage data should be done by the service layer
    }

    /**
     * Complete plan association validation with usage data (for service layer calls).
     * API: POST /api/v1/organizations/{id}/plans
     *
     * @param organization the organization
     * @param organizationPlan the organization plan entity
     * @param currentActivePlan the current active plan (if any)
     * @param currentUserCount current user count for the organization
     * @param currentTenantCount current tenant count for the organization
     * @param currentStorageUsage current storage usage for the organization
     * @param hasValidPaymentMethod whether organization has valid payment method
     * @param parentActivePlan parent organization's active plan (if applicable)
     * @param errors list to collect validation errors
     */
    public void validatePlanAssociationForOrganizationWithUsageData(Organization organization, OrganizationPlan organizationPlan,
                                                      OrganizationPlan currentActivePlan, int currentUserCount,
                                                      int currentTenantCount, long currentStorageUsage,
                                                      boolean hasValidPaymentMethod, OrganizationPlan parentActivePlan,
                                                      List<ValidationException.ValidationError> errors) {
        Plan plan = validatePlanExistsAndAvailable(organizationPlan.getPlanId(), errors);
        if (plan == null) {
            return;
        }

        validatePlanCompatibilityWithOrganization(organization, plan, errors);
        validatePlanTransitionRules(organization, plan, organizationPlan, currentActivePlan, errors);
        validatePlanBusinessConstraints(organization, plan, organizationPlan, currentUserCount, 
                                      currentTenantCount, currentStorageUsage, hasValidPaymentMethod, 
                                      parentActivePlan, errors);
    }

    // ========== CORE VALIDATION METHODS ==========

    private Plan validatePlanExistsAndAvailable(String planId, List<ValidationException.ValidationError> errors) {
        // Plan ID required validation is handled by @NotBlank annotation in OrganizationPlanRequestDto

        Plan plan = planService.findById(planId);

        if (plan == null) {
            errors.add(new ValidationException.ValidationError("planId", "Plan not found"));
            return null;
        }

        if (!plan.getIsActive()) {
            errors.add(new ValidationException.ValidationError("planId", "Plan is not active"));
            return null;
        }

        if (plan.getIsDeprecated() != null && plan.getIsDeprecated()) {
            errors.add(new ValidationException.ValidationError("planId", "Plan is deprecated"));
            return null;
        }

        return plan;
    }

    private void validatePlanCompatibilityWithOrganization(Organization organization, Plan plan, 
                                                          List<ValidationException.ValidationError> errors) {
        if (!isPlanCompatibleWithOrganizationType(plan, organization.getOrganizationType())) {
            errors.add(new ValidationException.ValidationError("planId", 
                "Plan is not compatible with organization type: " + organization.getOrganizationType()));
        }

        if (organization.getSetting() != null && organization.getSetting().getCountry() != null) {
            if (!planService.isPlanAvailableInRegion(plan.getId(), organization.getSetting().getCountry())) {
                errors.add(new ValidationException.ValidationError("planId", 
                    "Plan is not available in organization's region"));
            }
        }
    }

    private void validatePlanTransitionRules(Organization organization, Plan plan, OrganizationPlan organizationPlan,
                                           OrganizationPlan currentActivePlan, List<ValidationException.ValidationError> errors) {
        
        if (currentActivePlan != null) {
            Plan currentPlanEntity = planService.findById(currentActivePlan.getPlanId());
            
            if (currentActivePlan.getPlanId().equals(plan.getId())) {
                errors.add(new ValidationException.ValidationError("planId", 
                    "Organization is already on this plan"));
                return;
            }

            validatePlanTransitionTiming(currentActivePlan, organizationPlan, errors);
            validatePlanTransitionPath(currentPlanEntity, plan, errors);
        }
    }

    private void validatePlanTransitionTiming(OrganizationPlan currentPlan, OrganizationPlan newPlan, 
                                            List<ValidationException.ValidationError> errors) {
        if (newPlan.getValidFrom() != null && newPlan.getValidFrom().isAfter(Instant.now())) {
            Instant minimumAdvanceTime = Instant.now().plus(24, ChronoUnit.HOURS);
            if (newPlan.getValidFrom().isBefore(minimumAdvanceTime)) {
                errors.add(new ValidationException.ValidationError("validFrom", 
                    "Plan changes must be scheduled at least 24 hours in advance"));
            }
        }
    }

    private void validatePlanTransitionPath(Plan currentPlan, Plan newPlan, 
                                          List<ValidationException.ValidationError> errors) {
        if (!planService.isTransitionAllowed(currentPlan.getType(), newPlan.getType())) {
            errors.add(new ValidationException.ValidationError("planId", 
                "Direct transition from " + currentPlan.getType() + " to " + newPlan.getType() + " is not allowed"));
        }
    }

    private void validatePlanBusinessConstraints(Organization organization, Plan plan, OrganizationPlan organizationPlan,
                                               int currentUserCount, int currentTenantCount, long currentStorageUsage,
                                               boolean hasValidPaymentMethod, OrganizationPlan parentActivePlan,
                                               List<ValidationException.ValidationError> errors) {
        validatePlanFeaturesAgainstUsage(organization, plan, currentUserCount, currentTenantCount, currentStorageUsage, errors);
        validatePlanBillingConstraints(organization, plan, organizationPlan, hasValidPaymentMethod, errors);
        validatePlanCapacityConstraints(organization, plan, parentActivePlan, errors);
    }

    private void validatePlanFeaturesAgainstUsage(Organization organization, Plan plan,
                                                 int currentUserCount, int currentTenantCount, long currentStorageUsage,
                                                 List<ValidationException.ValidationError> errors) {

        if (plan.getMaxUsers() != null && currentUserCount > plan.getMaxUsers()) {
            errors.add(new ValidationException.ValidationError("planId", 
                "Plan user limit (" + plan.getMaxUsers() + ") is less than current user count (" + currentUserCount + ")"));
        }

        if (plan.getMaxTenants() != null && currentTenantCount > plan.getMaxTenants()) {
            errors.add(new ValidationException.ValidationError("planId", 
                "Plan tenant limit (" + plan.getMaxTenants() + ") is less than current tenant count (" + currentTenantCount + ")"));
        }

        if (plan.getStorageLimit() != null && currentStorageUsage > plan.getStorageLimit()) {
            errors.add(new ValidationException.ValidationError("planId", 
                "Plan storage limit is less than current usage"));
        }
    }

    private void validatePlanBillingConstraints(Organization organization, Plan plan, OrganizationPlan organizationPlan,
                                              boolean hasValidPaymentMethod, List<ValidationException.ValidationError> errors) {
        if (plan.getPrice() != null && plan.getPrice() > 0) {
            if (!hasValidPaymentMethod) {
                errors.add(new ValidationException.ValidationError("planId", 
                    "Valid payment method required for paid plans"));
            }
        }

        if (organizationPlan.getValidFrom() != null && organizationPlan.getValidTo() != null) {
            long planDurationDays = ChronoUnit.DAYS.between(organizationPlan.getValidFrom(), organizationPlan.getValidTo());
            if (!isValidBillingCycle(plan, planDurationDays)) {
                errors.add(new ValidationException.ValidationError("validTo", 
                    "Plan duration does not match valid billing cycles"));
            }
        }
    }

    private void validatePlanCapacityConstraints(Organization organization, Plan plan,
                                               OrganizationPlan parentActivePlan, List<ValidationException.ValidationError> errors) {
        if (!planService.hasPlanCapacity(plan.getId())) {
            errors.add(new ValidationException.ValidationError("planId", 
                "Plan has reached maximum capacity"));
        }

        if (StringUtils.hasText(organization.getParentOrganizationId())) {
            validateParentOrganizationPlanConstraints(organization, plan, parentActivePlan, errors);
        }
    }

    private void validateParentOrganizationPlanConstraints(Organization organization, Plan plan,
                                                          OrganizationPlan parentActivePlan, List<ValidationException.ValidationError> errors) {
        if (parentActivePlan != null) {
            Plan parentPlanEntity = planService.findById(parentActivePlan.getPlanId());
            
            if (!isPlanHierarchyValid(parentPlanEntity, plan)) {
                errors.add(new ValidationException.ValidationError("planId", 
                    "Child organization plan cannot exceed parent organization plan features"));
            }
        }
    }

    private boolean isPlanCompatibleWithOrganizationType(Plan plan, String organizationType) {
        return planService.isPlanCompatibleWithOrganizationType(plan.getId(), organizationType);
    }

    private boolean isPlanCompatibleWithOrganizationSize(Plan plan, String organizationSize) {
        return planService.isPlanCompatibleWithOrganizationSize(plan.getId(), organizationSize);
    }

    private boolean isValidBillingCycle(Plan plan, long durationDays) {
        int billingPeriod = plan.getValidityPeriod();
        
        if (plan.getValidityPeriodUnit().equals(PlanConstants.VALIDITY_UNIT_DAYS)) {
            return durationDays == billingPeriod;
        } else if (plan.getValidityPeriodUnit().equals(PlanConstants.VALIDITY_UNIT_MONTHS)) {
            long expectedDays = billingPeriod * 30L;
            return durationDays >= expectedDays - 1 && durationDays <= expectedDays + 1;
        } else if (plan.getValidityPeriodUnit().equals(PlanConstants.VALIDITY_UNIT_YEARS)) {
            long expectedDays = billingPeriod * 365L;
            return durationDays >= expectedDays - 1 && durationDays <= expectedDays + 1;
        }
        
        return false;
    }

    private boolean isPlanHierarchyValid(Plan parentPlan, Plan childPlan) {
        return planService.comparePlanLevels(parentPlan.getId(), childPlan.getId()) >= 0;
    }
}