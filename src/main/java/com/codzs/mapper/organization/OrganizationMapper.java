package com.codzs.mapper.organization;

import com.codzs.dto.organization.request.OrganizationCreateRequestDto;
import com.codzs.dto.organization.request.OrganizationUpdateRequestDto;
import com.codzs.framework.constant.CommonConstants;
import com.codzs.dto.organization.response.OrganizationResponseDto;
import com.codzs.dto.organization.response.OrganizationSummaryResponseDto;
import com.codzs.entity.organization.Organization;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * MapStruct mapper for Organization entity and DTOs.
 * Handles mapping between Organization entity, request DTOs, and response DTOs
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
public interface OrganizationMapper {

    // ========================= CREATE MAPPINGS =========================
    
    /**
     * Maps OrganizationCreateRequestDto to Organization entity.
     * Sets default values and handles business logic for new organizations.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", expression = "java(com.codzs.constant.organization.OrganizationStatusEnum.PENDING)")
    @Mapping(target = "expiresDate", ignore = true) // Will be calculated by business logic
    Organization toEntity(OrganizationCreateRequestDto requestDto);

    /**
     * Maps OrganizationCreateRequestDto to Organization entity.
     * Sets default values and handles business logic for new organizations.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", expression = "java(com.codzs.constant.organization.OrganizationStatusEnum.PENDING)")
    @Mapping(target = "expiresDate", ignore = true) // Will be calculated by business logic
    Organization toUpdateEntity(OrganizationUpdateRequestDto requestDto);

    // ========================= UPDATE MAPPINGS =========================
    
    /**
     * Maps OrganizationUpdateRequestDto to existing Organization entity.
     * Preserves existing audit fields and updates only provided fields.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true) // Status changes through separate endpoints
    @Mapping(target = "expiresDate", ignore = true) // Business logic handles this
    void updateEntity(@MappingTarget Organization organization, 
                     OrganizationUpdateRequestDto requestDto);


    // ========================= RESPONSE MAPPINGS =========================
    
    /**
     * Maps Organization entity to OrganizationResponseDto.
     * Handles all nested objects and ensures UTC date formatting.
     */
    @Mapping(source = "createdDate", target = "createdDate", dateFormat = CommonConstants.UTC_TIMESTAMP_PATTERN)
    @Mapping(source = "lastModifiedDate", target = "lastModifiedDate", dateFormat = CommonConstants.UTC_TIMESTAMP_PATTERN)
    @Mapping(source = "deletedDate", target = "deletedDate", dateFormat = CommonConstants.UTC_TIMESTAMP_PATTERN)
    @Mapping(source = "expiresDate", target = "expiresDate", dateFormat = CommonConstants.UTC_TIMESTAMP_PATTERN)
    @Named("toResponse")
    OrganizationResponseDto toResponse(Organization organization);

    /**
     * Maps list of Organization entities to list of response DTOs.
     */
    @IterableMapping(qualifiedByName = "toResponse")
    List<OrganizationResponseDto> toResponseList(List<Organization> organizations);

    // ========================= UTILITY METHODS =========================
    

    // ========================= SPECIALIZED MAPPINGS =========================
    
    /**
     * Maps Organization entity to simplified response for autocomplete.
     */
    // @Mapping(target = "description", ignore = true)
    @Mapping(target = "organizationType", ignore = true)
    @Mapping(target = "billingEmail", ignore = true)
    // @Mapping(target = "expiresDate", ignore = true)
    // @Mapping(target = "database", ignore = true)
    // @Mapping(target = "settings", ignore = true)
    // @Mapping(target = "metadata", ignore = true)
    // @Mapping(target = "domains", ignore = true)
    // @Mapping(target = "ownerUserIds", ignore = true)
    @Mapping(target = "parentOrganizationId", ignore = true)
    // @Mapping(target = "correlationId", ignore = true)
    @Named("toAutocompleteResponse")
    OrganizationSummaryResponseDto toAutocompleteResponse(Organization organization);

    /**
     * Maps list of Organization entities to autocomplete response list.
     */
    @IterableMapping(qualifiedByName = "toAutocompleteResponse")
    List<OrganizationResponseDto> toAutocompleteResponseList(List<Organization> organizations);

    // ========================= HIERARCHY MAPPINGS =========================
    
    /**
     * Maps Organization entity to hierarchy response.
     * Includes parent/child relationship information.
     */
    @Mapping(source = "createdDate", target = "createdDate", dateFormat = CommonConstants.UTC_TIMESTAMP_PATTERN)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "billingEmail", ignore = true)
    @Mapping(target = "expiresDate", ignore = true)
    @Mapping(target = "database", ignore = true)
    @Mapping(target = "settings", ignore = true)
    @Mapping(target = "metadata", ignore = true)
    @Mapping(target = "domains", ignore = true)
    @Mapping(target = "ownerUserIds", ignore = true)
    @Mapping(target = "correlationId", ignore = true)
    @Named("toHierarchyResponse")
    OrganizationResponseDto toHierarchyResponse(Organization organization);

    /**
     * Maps list of Organization entities to hierarchy response list.
     */
    @IterableMapping(qualifiedByName = "toHierarchyResponse")
    List<OrganizationResponseDto> toHierarchyResponseList(List<Organization> organizations);
}