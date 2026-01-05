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
    @Mapping(source = "validFrom", target = "validFrom", dateFormat = CommonConstants.UTC_TIMESTAMP_PATTERN)
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
    @Mapping(target = "organizationId", ignore = true)
    void updateEntity(@MappingTarget OrganizationPlan organizationPlan, 
                     OrganizationPlanRequestDto requestDto);

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

}