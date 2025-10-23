package com.codzs.mapper.organization;

import com.codzs.dto.organization.request.OrganizationPlanRequestDto;
import com.codzs.dto.organization.response.OrganizationPlanResponseDto;
import com.codzs.framework.constant.CommonConstants;
import com.codzs.entity.organization.OrganizationPlan;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * MapStruct mapper for OrganizationPlan entity and DTOs.
 * Handles mapping between OrganizationPlan entity, request DTOs, and response DTOs
 * with proper data transformations and UTC date handling.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
@Component
public interface OrganizationPlanMapper {

    // ========================= CREATE MAPPINGS =========================
    
    /**
     * Maps OrganizationPlanRequestDto to OrganizationPlan entity.
     * Sets default values for plan association.
     * Audit fields are automatically populated by Spring Data MongoDB auditing.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "validFrom", expression = "java(requestDto.getValidFrom() != null ? requestDto.getValidFrom() : java.time.Instant.now())")
    OrganizationPlan toEntity(OrganizationPlanRequestDto requestDto);

    /**
     * Post-mapping method to set non-audit fields for creation.
     */
    @AfterMapping
    default void setCreationFields(@MappingTarget OrganizationPlan organizationPlan) {
        // Ensure validFrom is set if not provided
        if (organizationPlan.getValidFrom() == null) {
            organizationPlan.setValidFrom(Instant.now());
        }
    }

    // ========================= UPDATE MAPPINGS =========================
    
    /**
     * Updates existing OrganizationPlan entity with request data.
     * Preserves creation audit fields and updates modification fields.
     * Audit fields are automatically updated by Spring Data MongoDB auditing.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organizationId", ignore = true) // Cannot change organization
    void updateEntity(@MappingTarget OrganizationPlan organizationPlan, 
                     OrganizationPlanRequestDto requestDto);

    /**
     * Post-mapping method to set non-audit fields for updates.
     */

    // ========================= RESPONSE MAPPINGS =========================
    
    /**
     * Maps OrganizationPlan entity to OrganizationPlanResponseDto.
     * Handles UTC date formatting and includes all plan information.
     */
    @Mapping(source = "validFrom", target = "validFrom", dateFormat = CommonConstants.UTC_TIMESTAMP_PATTERN)
    @Mapping(source = "validTo", target = "validTo", dateFormat = CommonConstants.UTC_TIMESTAMP_PATTERN)
    @Mapping(source = "deletedDate", target = "deletedDate", dateFormat = CommonConstants.UTC_TIMESTAMP_PATTERN)
    @Named("toResponse")
    OrganizationPlanResponseDto toResponse(OrganizationPlan organizationPlan);

    /**
     * Maps list of OrganizationPlan entities to list of response DTOs.
     */
    @IterableMapping(qualifiedByName = "toResponse")
    List<OrganizationPlanResponseDto> toResponseList(List<OrganizationPlan> organizationPlans);

    // ========================= SIMPLIFIED RESPONSE MAPPINGS =========================
    
    /**
     * Maps OrganizationPlan entity to simplified response for plan overview.
     */
    @Mapping(source = "validFrom", target = "validFrom", dateFormat = CommonConstants.UTC_TIMESTAMP_PATTERN)
    @Mapping(source = "validTo", target = "validTo", dateFormat = CommonConstants.UTC_TIMESTAMP_PATTERN)
    @Mapping(target = "organizationId", ignore = true)
    @Mapping(target = "comment", ignore = true)
    @Mapping(target = "correlationId", ignore = true)
    OrganizationPlanResponseDto toSimplifiedResponse(OrganizationPlan organizationPlan);

    // ========================= PLAN HISTORY MAPPINGS =========================
    
    /**
     * Maps OrganizationPlan entity to plan history response.
     * Includes historical information and status changes.
     */
    @Mapping(source = "validFrom", target = "validFrom", dateFormat = CommonConstants.UTC_TIMESTAMP_PATTERN)
    @Mapping(source = "validTo", target = "validTo", dateFormat = CommonConstants.UTC_TIMESTAMP_PATTERN)
    @Mapping(source = "createdDate", target = "createdDate", dateFormat = CommonConstants.UTC_TIMESTAMP_PATTERN)
    @Mapping(target = "organizationId", ignore = true)
    @Mapping(target = "correlationId", ignore = true)
    @Named("toPlanHistoryResponse")
    OrganizationPlanResponseDto toPlanHistoryResponse(OrganizationPlan organizationPlan);

    /**
     * Maps list of OrganizationPlan entities to plan history response list.
     */
    @IterableMapping(qualifiedByName = "toPlanHistoryResponse")
    List<OrganizationPlanResponseDto> toPlanHistoryResponseList(List<OrganizationPlan> organizationPlans);

    // ========================= UTILITY METHODS =========================
    
    /**
     * Converts Instant to UTC formatted string.
     */
    default String instantToUtcString(Instant instant) {
        return instant != null ? instant.toString() : null;
    }

    /**
     * Converts UTC formatted string to Instant.
     */
    default Instant utcStringToInstant(String utcString) {
        return utcString != null && !utcString.trim().isEmpty() ? Instant.parse(utcString) : null;
    }


    /**
     * Checks if plan is currently active and valid.
     */
    default boolean isCurrentlyValid(OrganizationPlan organizationPlan) {
        if (organizationPlan == null || !organizationPlan.getIsActive()) {
            return false;
        }
        
        Instant now = Instant.now();
        
        // Check validFrom
        if (organizationPlan.getValidFrom() != null && now.isBefore(organizationPlan.getValidFrom())) {
            return false;
        }
        
        // Check validTo
        if (organizationPlan.getValidTo() != null && now.isAfter(organizationPlan.getValidTo())) {
            return false;
        }
        
        return true;
    }

    /**
     * Checks if plan is expired.
     */
    default boolean isExpired(OrganizationPlan organizationPlan) {
        if (organizationPlan == null || organizationPlan.getValidTo() == null) {
            return false;
        }
        
        return Instant.now().isAfter(organizationPlan.getValidTo());
    }

    /**
     * Calculates plan duration in days.
     */
    default Long calculatePlanDurationDays(OrganizationPlan organizationPlan) {
        if (organizationPlan == null || 
            organizationPlan.getValidFrom() == null || 
            organizationPlan.getValidTo() == null) {
            return null;
        }
        
        return java.time.Duration.between(
            organizationPlan.getValidFrom(), 
            organizationPlan.getValidTo()
        ).toDays();
    }

    /**
     * Gets remaining days for the plan.
     */
    default Long getRemainingDays(OrganizationPlan organizationPlan) {
        if (organizationPlan == null || organizationPlan.getValidTo() == null) {
            return null;
        }
        
        Instant now = Instant.now();
        if (now.isAfter(organizationPlan.getValidTo())) {
            return 0L; // Expired
        }
        
        return java.time.Duration.between(now, organizationPlan.getValidTo()).toDays();
    }

    // ========================= BUSINESS LOGIC HELPERS =========================
    
    /**
     * Deactivates organization plan and sets update fields.
     * Note: lastModifiedBy and lastModifiedDate are automatically updated by Spring Data auditing.
     */
    @AfterMapping
    default void handlePlanDeactivation(@MappingTarget OrganizationPlan organizationPlan, 
                                       @Context boolean shouldDeactivate) {
        if (shouldDeactivate) {
            organizationPlan.setIsActive(false);
        }
    }

    /**
     * Activates organization plan and sets update fields.
     * Note: lastModifiedBy and lastModifiedDate are automatically updated by Spring Data auditing.
     */
    @AfterMapping
    default void handlePlanActivation(@MappingTarget OrganizationPlan organizationPlan, 
                                     @Context boolean shouldActivate) {
        if (shouldActivate) {
            organizationPlan.setIsActive(true);
            
            // Set validFrom to now if not already set
            if (organizationPlan.getValidFrom() == null) {
                organizationPlan.setValidFrom(Instant.now());
            }
        }
    }
}