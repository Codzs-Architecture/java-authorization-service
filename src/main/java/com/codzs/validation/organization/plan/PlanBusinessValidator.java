package com.codzs.validation.organization.plan;

import com.codzs.constant.plan.PlanConstants;
import com.codzs.constant.plan.PlanTypeEnum;
import com.codzs.entity.organization.Organization;
import com.codzs.entity.organization.OrganizationPlan;
import com.codzs.entity.plan.Plan;
import com.codzs.exception.validation.ValidationException;
import com.codzs.service.organization.OrganizationPlanService;
import com.codzs.service.plan.PlanService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Business validator for Plan-related operations within organizations.
 * Focuses on plan association for organization APIs.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Component
public class PlanBusinessValidator {
    
    private final PlanService planService;
    private final OrganizationPlanService organizationPlanService;
    private final PlanTypeEnum planTypeEnum;

    @Autowired
    public PlanBusinessValidator(
        PlanService planService, 
        OrganizationPlanService organizationPlanService, 
        PlanTypeEnum planTypeEnum
    ) {
        this.planService = planService;
        this.organizationPlanService = organizationPlanService;
        this.planTypeEnum = planTypeEnum;
    }

    // ========== ENTRY POINT METHODS FOR ORGANIZATION APIs ==========

    /**
     * Validates plan association for organization.
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
        validatePlanTransitionRules(organization, plan, organizationPlan, errors);
        validatePlanBusinessConstraints(organization, plan, organizationPlan, errors);
    }

    // ========== CORE VALIDATION METHODS ==========

    private Plan validatePlanExistsAndAvailable(String planId, List<ValidationException.ValidationError> errors) {
        if (!StringUtils.hasText(planId)) {
            errors.add(new ValidationException.ValidationError("planId", "Plan ID is required"));
            return null;
        }

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

        if (organization.getMetadata() != null && organization.getMetadata().getSize() != null) {
            if (!isPlanCompatibleWithOrganizationSize(plan, organization.getMetadata().getSize())) {
                errors.add(new ValidationException.ValidationError("planId", 
                    "Plan is not compatible with organization size"));
            }
        }

        if (organization.getSettings() != null && organization.getSettings().getCountry() != null) {
            if (!planService.isPlanAvailableInRegion(plan.getId(), organization.getSettings().getCountry())) {
                errors.add(new ValidationException.ValidationError("planId", 
                    "Plan is not available in organization's region"));
            }
        }
    }

    private void validatePlanTransitionRules(Organization organization, Plan plan, OrganizationPlan organizationPlan, 
                                           List<ValidationException.ValidationError> errors) {
        OrganizationPlan currentPlan = organizationPlanService.getCurrentActivePlan(organization.getId());
        
        if (currentPlan != null) {
            Plan currentPlanEntity = planService.findById(currentPlan.getPlanId());
            
            if (currentPlanEntity.getId().equals(plan.getId())) {
                errors.add(new ValidationException.ValidationError("planId", 
                    "Organization is already on this plan"));
                return;
            }

            validatePlanTransitionTiming(currentPlan, organizationPlan, errors);
            validatePlanTransitionPath(currentPlanEntity, plan, errors);
        }
    }

    private void validatePlanTransitionTiming(OrganizationPlan currentPlan, OrganizationPlan newPlan, 
                                            List<ValidationException.ValidationError> errors) {
        if (currentPlan.getValidFrom() != null) {
            Instant minimumTermEnd = currentPlan.getValidFrom().plus(30, ChronoUnit.DAYS);
            if (Instant.now().isBefore(minimumTermEnd)) {
                errors.add(new ValidationException.ValidationError("planId", 
                    "Cannot change plan before minimum term completion"));
            }
        }

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
        Boolean currentPlanType = planTypeEnum.isValidOption(currentPlan.getType());
        Boolean newPlanType = planTypeEnum.isValidOption(newPlan.getType());

        if (!planService.isTransitionAllowed(currentPlan.getType(), newPlan.getType())) {
            errors.add(new ValidationException.ValidationError("planId", 
                "Direct transition from " + currentPlanType + " to " + newPlanType + " is not allowed"));
        }
    }

    private void validatePlanBusinessConstraints(Organization organization, Plan plan, OrganizationPlan organizationPlan, 
                                               List<ValidationException.ValidationError> errors) {
        validatePlanFeaturesAgainstUsage(organization, plan, errors);
        validatePlanBillingConstraints(organization, plan, organizationPlan, errors);
        validatePlanCapacityConstraints(organization, plan, errors);
    }

    private void validatePlanFeaturesAgainstUsage(Organization organization, Plan plan, 
                                                 List<ValidationException.ValidationError> errors) {
        int currentUserCount = organizationPlanService.getCurrentUserCount(organization.getId());

        if (plan.getMaxUsers() != null && currentUserCount > plan.getMaxUsers()) {
            errors.add(new ValidationException.ValidationError("planId", 
                "Plan user limit (" + plan.getMaxUsers() + ") is less than current user count (" + currentUserCount + ")"));
        }

        int currentTenantCount = organizationPlanService.getCurrentTenantCount(organization.getId());
        if (plan.getMaxTenants() != null && currentTenantCount > plan.getMaxTenants()) {
            errors.add(new ValidationException.ValidationError("planId", 
                "Plan tenant limit (" + plan.getMaxTenants() + ") is less than current tenant count (" + currentTenantCount + ")"));
        }

        long currentStorageUsage = organizationPlanService.getCurrentStorageUsage(organization.getId());
        if (plan.getStorageLimit() != null && currentStorageUsage > plan.getStorageLimit()) {
            errors.add(new ValidationException.ValidationError("planId", 
                "Plan storage limit is less than current usage"));
        }
    }

    private void validatePlanBillingConstraints(Organization organization, Plan plan, OrganizationPlan organizationPlan, 
                                              List<ValidationException.ValidationError> errors) {
        if (plan.getPrice() != null && plan.getPrice() > 0) {
            if (!organizationPlanService.hasValidPaymentMethod(organization.getId())) {
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
                                               List<ValidationException.ValidationError> errors) {
        if (!planService.hasPlanCapacity(plan.getId())) {
            errors.add(new ValidationException.ValidationError("planId", 
                "Plan has reached maximum capacity"));
        }

        if (StringUtils.hasText(organization.getParentOrganizationId())) {
            validateParentOrganizationPlanConstraints(organization, plan, errors);
        }
    }

    private void validateParentOrganizationPlanConstraints(Organization organization, Plan plan, 
                                                          List<ValidationException.ValidationError> errors) {
        OrganizationPlan parentPlan = organizationPlanService.getCurrentActivePlan(organization.getParentOrganizationId());
        if (parentPlan != null) {
            Plan parentPlanEntity = planService.findById(parentPlan.getPlanId());
            
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
        
        long expectedDays = 0;
        if (plan.getValidityPeriodUnit() == PlanConstants.VALIDITY_UNIT_DAYS) {
            return expectedDays == billingPeriod;
        }   else if (plan.getValidityPeriodUnit() == PlanConstants.VALIDITY_UNIT_MONTHS) {
            return expectedDays >= billingPeriod * 30L - 1 && durationDays <= billingPeriod * 30L + 1;
        } else if (plan.getValidityPeriodUnit() == PlanConstants.VALIDITY_UNIT_YEARS) {
            return expectedDays >= billingPeriod * 365L - 1 && durationDays <= billingPeriod * 365L + 1;
        }
        
        return Math.abs(durationDays - expectedDays) <= 1;
    }

    private boolean isPlanHierarchyValid(Plan parentPlan, Plan childPlan) {
        return planService.comparePlanLevels(parentPlan.getId(), childPlan.getId()) >= 0;
    }
}