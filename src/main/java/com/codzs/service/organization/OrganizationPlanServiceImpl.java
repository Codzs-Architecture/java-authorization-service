package com.codzs.service.organization;

import com.codzs.entity.organization.Organization;
import com.codzs.entity.organization.OrganizationPlan;
import com.codzs.exception.validation.ValidationException;
import com.codzs.repository.organization.OrganizationPlanRepository;
import com.codzs.service.plan.PlanService;
import com.codzs.validation.organization.OrganizationPlanBusinessValidator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for OrganizationPlan-related business operations.
 * Manages organization-plan associations with proper business validation
 * and transaction management. Follows entity-first design pattern.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class OrganizationPlanServiceImpl implements OrganizationPlanService {

    private final OrganizationPlanRepository organizationPlanRepository;
    private final OrganizationService organizationService;
    private final PlanService planService;
    private final OrganizationPlanBusinessValidator organizationPlanBusinessValidator;

    @Autowired
    public OrganizationPlanServiceImpl(OrganizationPlanRepository organizationPlanRepository,
                                     OrganizationService organizationService,
                                     PlanService planService,
                                     OrganizationPlanBusinessValidator organizationPlanBusinessValidator) {
        this.organizationPlanRepository = organizationPlanRepository;
        this.organizationService = organizationService;
        this.planService = planService;
        this.organizationPlanBusinessValidator = organizationPlanBusinessValidator;
    }

    // ========== API FLOW METHODS ==========

    @Override
    @Transactional
    public OrganizationPlan associatePlanWithOrganization(Organization organization, OrganizationPlan organizationPlan) {
        log.debug("Associating plan {} with organization ID: {}", organizationPlan.getPlanId(), organization.getId());
        
        // Set organization ID from the organization entity
        organizationPlan.setOrganizationId(organization.getId());
        
        // Business validation as first step
        organizationPlanBusinessValidator.validatePlanAssociationForOrganization(organization, organizationPlan, new ArrayList<>());
        
        // Deactivate any existing active plans if this is an immediate activation
        if (organizationPlan.getIsActive() && isImmediateActivation(organizationPlan)) {
            deactivateExistingActivePlans(organization.getId(), organizationPlan.getCreatedBy());
        }
        
        // Apply plan association business logic
        applyPlanAssociationBusinessLogic(organization, organizationPlan);
        
        // Save organization plan association
        OrganizationPlan savedPlan = organizationPlanRepository.save(organizationPlan);
        
        log.info("Associated plan {} with organization ID: {} with association ID: {}", 
                organizationPlan.getPlanId(), organization.getId(), savedPlan.getId());
        
        return savedPlan;
    }

    @Override
    @Transactional
    public OrganizationPlan updateOrganizationPlan(Organization organization, OrganizationPlan organizationPlan) {
        log.debug("Updating organization plan association ID: {} for organization ID: {}", 
                organizationPlan.getId(), organization.getId());
        
        // Retrieve existing organization plan
        OrganizationPlan existingPlan = findByOrganizationAndId(organization.getId(), organizationPlan.getId());
        if (existingPlan == null) {
            throw new ValidationException("Organization plan association not found");
        }
        
        // Business validation for update
        validateOrganizationPlanUpdateFlow(organization, organizationPlan, existingPlan);
        
        // Update existing plan with new data
        updatePlanFields(existingPlan, organizationPlan);
        
        // Apply update business logic
        applyPlanUpdateBusinessLogic(organization, existingPlan);
        
        // Save updated organization plan
        OrganizationPlan updatedPlan = organizationPlanRepository.save(existingPlan);
        
        log.info("Updated organization plan association ID: {} for organization ID: {}", 
                organizationPlan.getId(), organization.getId());
        
        return updatedPlan;
    }

    @Override
    public OrganizationPlan getCurrentActivePlan(String organizationId) {
        log.debug("Getting current active plan for organization ID: {}", organizationId);
        
        return organizationPlanRepository.findCurrentValidPlan(organizationId, Instant.now())
                .orElse(null);
    }

    @Override
    public Page<OrganizationPlan> getOrganizationPlanHistory(String organizationId, Pageable pageable) {
        log.debug("Getting plan history for organization ID: {}", organizationId);
        
        return organizationPlanRepository.findByOrganizationIdAndDeletedOnIsNullOrderByCreatedDateDesc(
                organizationId, pageable);
    }

    @Override
    public Page<OrganizationPlan> getOrganizationPlanHistory(String organizationId, Instant startDate, 
                                                           Instant endDate, Pageable pageable) {
        log.debug("Getting plan history for organization ID: {} from {} to {}", 
                organizationId, startDate, endDate);
        
        return organizationPlanRepository.findByOrganizationIdWithDateRange(
                organizationId, startDate, endDate, pageable);
    }

    @Override
    @Transactional
    public OrganizationPlan activateOrganizationPlan(String organizationId, String planAssociationId) {
        log.debug("Activating organization plan association ID: {} for organization ID: {}", 
                planAssociationId, organizationId);
        
        // Retrieve organization plan
        OrganizationPlan organizationPlan = findByOrganizationAndId(organizationId, planAssociationId);
        if (organizationPlan == null) {
            throw new ValidationException("Organization plan association not found");
        }
        
        // Business validation for activation
        validatePlanActivationFlow(organizationId, organizationPlan);
        
        // Deactivate other active plans
        deactivateExistingActivePlans(organizationId, organizationPlan.getLastModifiedBy());
        
        // Activate the plan
        organizationPlan.activate(organizationPlan.getLastModifiedBy());
        
        // Apply activation business logic
        applyPlanActivationBusinessLogic(organizationPlan);
        
        // Save activated plan
        OrganizationPlan activatedPlan = organizationPlanRepository.save(organizationPlan);
        
        log.info("Activated organization plan association ID: {} for organization ID: {}", 
                planAssociationId, organizationId);
        
        return activatedPlan;
    }

    @Override
    @Transactional
    public OrganizationPlan deactivateOrganizationPlan(String organizationId, String planAssociationId) {
        log.debug("Deactivating organization plan association ID: {} for organization ID: {}", 
                planAssociationId, organizationId);
        
        // Retrieve organization plan
        OrganizationPlan organizationPlan = findByOrganizationAndId(organizationId, planAssociationId);
        if (organizationPlan == null) {
            throw new ValidationException("Organization plan association not found");
        }
        
        // Business validation for deactivation
        validatePlanDeactivationFlow(organizationId, organizationPlan);
        
        // Deactivate the plan
        organizationPlan.deactivate(organizationPlan.getLastModifiedBy());
        
        // Apply deactivation business logic
        applyPlanDeactivationBusinessLogic(organizationPlan);
        
        // Save deactivated plan
        OrganizationPlan deactivatedPlan = organizationPlanRepository.save(organizationPlan);
        
        log.info("Deactivated organization plan association ID: {} for organization ID: {}", 
                planAssociationId, organizationId);
        
        return deactivatedPlan;
    }

    @Override
    @Transactional
    public OrganizationPlan changeOrganizationPlan(Organization organization, OrganizationPlan newOrganizationPlan) {
        log.debug("Changing plan for organization ID: {} to plan ID: {}", 
                organization.getId(), newOrganizationPlan.getPlanId());
        
        // Set organization ID
        newOrganizationPlan.setOrganizationId(organization.getId());
        
        // Business validation for plan change
        organizationPlanBusinessValidator.validatePlanAssociationForOrganization(organization, newOrganizationPlan, new ArrayList<>());
        
        // Deactivate existing active plans
        deactivateExistingActivePlans(organization.getId(), newOrganizationPlan.getCreatedBy());
        
        // Create new plan association
        newOrganizationPlan.setIsActive(true);
        if (newOrganizationPlan.getValidFrom() == null) {
            newOrganizationPlan.setValidFrom(Instant.now());
        }
        
        // Apply plan change business logic
        applyPlanChangeBusinessLogic(organization, newOrganizationPlan);
        
        // Save new plan association
        OrganizationPlan savedPlan = organizationPlanRepository.save(newOrganizationPlan);
        
        log.info("Changed plan for organization ID: {} to plan ID: {} with association ID: {}", 
                organization.getId(), newOrganizationPlan.getPlanId(), savedPlan.getId());
        
        return savedPlan;
    }

    @Override
    @Transactional
    public OrganizationPlan removeOrganizationPlan(String organizationId, String planAssociationId, String deletedBy) {
        log.debug("Removing organization plan association ID: {} for organization ID: {}", 
                planAssociationId, organizationId);
        
        // Retrieve organization plan
        OrganizationPlan organizationPlan = findByOrganizationAndId(organizationId, planAssociationId);
        if (organizationPlan == null) {
            throw new ValidationException("Organization plan association not found");
        }
        
        // Business validation for removal
        validatePlanRemovalFlow(organizationId, organizationPlan);
        
        // Perform soft delete
        organizationPlan.softDelete(deletedBy);
        
        // Apply removal business logic
        applyPlanRemovalBusinessLogic(organizationPlan);
        
        // Save deleted plan
        OrganizationPlan deletedPlan = organizationPlanRepository.save(organizationPlan);
        
        log.info("Removed organization plan association ID: {} for organization ID: {}", 
                planAssociationId, organizationId);
        
        return deletedPlan;
    }

    @Override
    @Transactional
    public List<OrganizationPlan> processExpiredOrganizationPlans() {
        log.debug("Processing expired organization plans");
        
        List<OrganizationPlan> expiredPlans = organizationPlanRepository.findExpiredActivePlans(Instant.now());
        
        for (OrganizationPlan expiredPlan : expiredPlans) {
            log.info("Processing expired plan association ID: {} for organization ID: {}", 
                    expiredPlan.getId(), expiredPlan.getOrganizationId());
            
            // Deactivate expired plan
            expiredPlan.deactivate("system");
            
            // Apply expiration business logic
            applyPlanExpirationBusinessLogic(expiredPlan);
            
            organizationPlanRepository.save(expiredPlan);
        }
        
        log.info("Processed {} expired organization plans", expiredPlans.size());
        
        return expiredPlans;
    }

    // ========== UTILITY METHODS FOR BUSINESS VALIDATION ==========

    @Override
    public OrganizationPlan findById(String planAssociationId) {
        return organizationPlanRepository.findById(planAssociationId)
                .filter(plan -> plan.getDeletedOn() == null)
                .orElse(null);
    }

    @Override
    public OrganizationPlan findByOrganizationAndId(String organizationId, String planAssociationId) {
        Optional<OrganizationPlan> planOpt = organizationPlanRepository.findById(planAssociationId);
        
        if (planOpt.isEmpty()) {
            return null;
        }
        
        OrganizationPlan plan = planOpt.get();
        
        // Verify the plan belongs to the organization and is not deleted
        if (plan.getDeletedOn() != null || !organizationId.equals(plan.getOrganizationId())) {
            return null;
        }
        
        return plan;
    }

    @Override
    public boolean hasActivePlan(String organizationId, String planId) {
        return organizationPlanRepository.existsByOrganizationIdAndPlanIdAndIsActiveTrueAndDeletedOnIsNull(
                organizationId, planId);
    }

    @Override
    public boolean hasConflictingPlans(String organizationId, Instant validFrom, Instant validTo, 
                                     String excludePlanAssociationId) {
        List<OrganizationPlan> conflictingPlans = organizationPlanRepository.findConflictingPlans(
                organizationId, validFrom, validTo);
        
        // Exclude the plan being updated from conflict check
        if (StringUtils.hasText(excludePlanAssociationId)) {
            conflictingPlans.removeIf(plan -> excludePlanAssociationId.equals(plan.getId()));
        }
        
        return !conflictingPlans.isEmpty();
    }

    @Override
    public int getCurrentUserCount(String organizationId) {
        // TODO: Implement user count when user service is available
        log.debug("User count check not implemented yet for organization ID: {}", organizationId);
        return 0;
    }

    @Override
    public int getCurrentTenantCount(String organizationId) {
        // TODO: Implement tenant count when tenant service is available
        log.debug("Tenant count check not implemented yet for organization ID: {}", organizationId);
        return 0;
    }

    @Override
    public long getCurrentStorageUsage(String organizationId) {
        // TODO: Implement storage usage when storage service is available
        log.debug("Storage usage check not implemented yet for organization ID: {}", organizationId);
        return 0L;
    }

    @Override
    public boolean hasValidPaymentMethod(String organizationId) {
        // TODO: Implement payment method check when payment service is available
        log.debug("Payment method check not implemented yet for organization ID: {}", organizationId);
        return true; // Assume valid for now
    }

    @Override
    public long getActivePlanUsageCount(String planId) {
        return organizationPlanRepository.countActiveUsageForPlan(planId, Instant.now());
    }

    @Override
    public List<OrganizationPlan> getAllOrganizationPlans(String organizationId) {
        return organizationPlanRepository.findByOrganizationIdAndDeletedOnIsNull(organizationId);
    }

    @Override
    public boolean hasAnyPlans(String organizationId) {
        return organizationPlanRepository.existsByOrganizationId(organizationId);
    }

    @Override
    public OrganizationPlan getLatestPlan(String organizationId) {
        return organizationPlanRepository.findTopByOrganizationIdAndDeletedOnIsNullOrderByCreatedDateDesc(organizationId)
                .orElse(null);
    }

    // ========== PRIVATE HELPER METHODS ==========

    private boolean isImmediateActivation(OrganizationPlan organizationPlan) {
        return organizationPlan.getValidFrom() == null || 
               !organizationPlan.getValidFrom().isAfter(Instant.now());
    }

    private void deactivateExistingActivePlans(String organizationId, String lastModifiedBy) {
        List<OrganizationPlan> activePlans = organizationPlanRepository.findActivePlansForOrganization(organizationId);
        
        for (OrganizationPlan activePlan : activePlans) {
            activePlan.deactivate(lastModifiedBy);
            organizationPlanRepository.save(activePlan);
            
            log.debug("Deactivated existing plan association ID: {} for organization ID: {}", 
                    activePlan.getId(), organizationId);
        }
    }

    private void updatePlanFields(OrganizationPlan existingPlan, OrganizationPlan updatedPlan) {
        // Update allowed fields
        if (updatedPlan.getComment() != null) {
            existingPlan.setComment(updatedPlan.getComment());
        }
        if (updatedPlan.getValidFrom() != null) {
            existingPlan.setValidFrom(updatedPlan.getValidFrom());
        }
        if (updatedPlan.getValidTo() != null) {
            existingPlan.setValidTo(updatedPlan.getValidTo());
        }
        if (updatedPlan.getIsActive() != null) {
            existingPlan.setIsActive(updatedPlan.getIsActive());
        }
    }

    // ========== VALIDATION METHODS ==========

    private void validateOrganizationPlanUpdateFlow(Organization organization, OrganizationPlan updatedPlan, 
                                                   OrganizationPlan existingPlan) {
        // Validate that organization and plan IDs cannot be changed
        if (!organization.getId().equals(existingPlan.getOrganizationId())) {
            throw new ValidationException("Cannot change organization for existing plan association");
        }
        
        // Validate validity period changes
        if (hasValidityPeriodChanged(existingPlan, updatedPlan)) {
            validateValidityPeriodChange(organization.getId(), updatedPlan, existingPlan.getId());
        }
        
        log.debug("Validated organization plan update for association ID: {}", existingPlan.getId());
    }

    private void validatePlanActivationFlow(String organizationId, OrganizationPlan organizationPlan) {
        if (organizationPlan.getIsActive()) {
            throw new ValidationException("Plan association is already active");
        }
        
        if (organizationPlan.isExpired()) {
            throw new ValidationException("Cannot activate expired plan association");
        }
        
        log.debug("Validated plan activation for association ID: {}", organizationPlan.getId());
    }

    private void validatePlanDeactivationFlow(String organizationId, OrganizationPlan organizationPlan) {
        if (!organizationPlan.getIsActive()) {
            throw new ValidationException("Plan association is already inactive");
        }
        
        log.debug("Validated plan deactivation for association ID: {}", organizationPlan.getId());
    }

    private void validatePlanRemovalFlow(String organizationId, OrganizationPlan organizationPlan) {
        // Can remove any plan association (active or inactive)
        log.debug("Validated plan removal for association ID: {}", organizationPlan.getId());
    }

    private boolean hasValidityPeriodChanged(OrganizationPlan existingPlan, OrganizationPlan updatedPlan) {
        return (updatedPlan.getValidFrom() != null && !updatedPlan.getValidFrom().equals(existingPlan.getValidFrom())) ||
               (updatedPlan.getValidTo() != null && !updatedPlan.getValidTo().equals(existingPlan.getValidTo()));
    }

    private void validateValidityPeriodChange(String organizationId, OrganizationPlan updatedPlan, 
                                            String excludePlanAssociationId) {
        if (hasConflictingPlans(organizationId, updatedPlan.getValidFrom(), updatedPlan.getValidTo(), 
                               excludePlanAssociationId)) {
            throw new ValidationException("Validity period conflicts with existing plan associations");
        }
    }

    // ========== BUSINESS LOGIC METHODS ==========

    private void applyPlanAssociationBusinessLogic(Organization organization, OrganizationPlan organizationPlan) {
        // Set default validity period if not provided
        if (organizationPlan.getValidFrom() == null) {
            organizationPlan.setValidFrom(Instant.now());
        }
        
        log.debug("Applied plan association business logic for organization: {}", organization.getName());
    }

    private void applyPlanUpdateBusinessLogic(Organization organization, OrganizationPlan organizationPlan) {
        // Apply any additional plan update business logic here
        log.debug("Applied plan update business logic for organization: {}", organization.getName());
    }

    private void applyPlanActivationBusinessLogic(OrganizationPlan organizationPlan) {
        // Apply any additional plan activation business logic here
        log.debug("Applied plan activation business logic for association ID: {}", organizationPlan.getId());
    }

    private void applyPlanDeactivationBusinessLogic(OrganizationPlan organizationPlan) {
        // Apply any additional plan deactivation business logic here
        log.debug("Applied plan deactivation business logic for association ID: {}", organizationPlan.getId());
    }

    private void applyPlanChangeBusinessLogic(Organization organization, OrganizationPlan newOrganizationPlan) {
        // Apply any additional plan change business logic here
        log.debug("Applied plan change business logic for organization: {}", organization.getName());
    }

    private void applyPlanRemovalBusinessLogic(OrganizationPlan organizationPlan) {
        // Apply any additional plan removal business logic here
        log.debug("Applied plan removal business logic for association ID: {}", organizationPlan.getId());
    }

    private void applyPlanExpirationBusinessLogic(OrganizationPlan organizationPlan) {
        // Apply any additional plan expiration business logic here
        log.debug("Applied plan expiration business logic for association ID: {}", organizationPlan.getId());
    }
}