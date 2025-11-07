package com.codzs.mapper.organization;

import com.codzs.dto.organization.request.OrganizationCreateRequestDto;
import com.codzs.dto.organization.request.OrganizationUpdateRequestDto;
import com.codzs.framework.constant.CommonConstants;
import com.codzs.framework.mapper.BaseMapper;
import com.codzs.dto.organization.response.OrganizationResponseDto;
import com.codzs.dto.organization.response.OrganizationSummaryResponseDto;
import com.codzs.entity.organization.DatabaseConfig;
import com.codzs.entity.organization.Organization;
import com.codzs.entity.organization.OrganizationMetadata;
import com.codzs.entity.organization.OrganizationSetting;

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
public interface OrganizationMapper extends BaseMapper {

    // ========================= CREATE MAPPINGS =========================
    
    /**
     * Maps OrganizationCreateRequestDto to Organization entity.
     * Sets default values and handles business logic for new organizations.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "expiresDate", target = "expiresDate", dateFormat = CommonConstants.UTC_TIMESTAMP_PATTERN)
    @Mapping(target = "createdDate", expression = "java(generateCurrentDateTime())")
    @Mapping(target = "createdBy", expression = "java(getCurrentUserId())")
    @Mapping(target = "lastModifiedDate", expression = "java(generateCurrentDateTime())")
    @Mapping(target = "lastModifiedBy", expression = "java(getCurrentUserId())")
    Organization toEntity(OrganizationCreateRequestDto requestDto);

    /**
     * Maps OrganizationCreateRequestDto to Organization entity.
     * Sets default values and handles business logic for new organizations.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "expiresDate", target = "expiresDate", dateFormat = CommonConstants.UTC_TIMESTAMP_PATTERN)
    @Mapping(target = "lastModifiedDate", expression = "java(generateCurrentDateTime())")
    @Mapping(target = "lastModifiedBy", expression = "java(getCurrentUserId())")
    Organization toUpdateEntity(OrganizationUpdateRequestDto requestDto);

    // ========================= UPDATE MAPPINGS =========================
    
    /**
     * Maps OrganizationUpdateRequestDto to existing Organization entity.
     * Preserves existing audit fields and updates only provided fields.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(source = "expiresDate", target = "expiresDate", dateFormat = CommonConstants.UTC_TIMESTAMP_PATTERN)
    @Mapping(target = "lastModifiedDate", expression = "java(generateCurrentDateTime())")
    @Mapping(target = "lastModifiedBy", expression = "java(getCurrentUserId())")
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

    // ========================= SPECIALIZED MAPPINGS =========================
    
    /**
     * Maps Organization entity to simplified response for autocomplete.
     */
    @Named("toAutocompleteResponse")
    OrganizationSummaryResponseDto toAutocompleteResponse(Organization organization);

    /**
     * Maps list of Organization entities to autocomplete response list.
     */
    @IterableMapping(qualifiedByName = "toAutocompleteResponse")
    List<OrganizationSummaryResponseDto> toAutocompleteResponseList(List<Organization> organizations);

    // ========================= HIERARCHY MAPPINGS =========================
    
    /**
     * Maps Organization entity to hierarchy response.
     * Includes parent/child relationship information.
     */
    @Mapping(source = "createdDate", target = "createdDate", dateFormat = CommonConstants.UTC_TIMESTAMP_PATTERN)
    @Mapping(source = "lastModifiedDate", target = "lastModifiedDate", dateFormat = CommonConstants.UTC_TIMESTAMP_PATTERN)
    @Mapping(source = "deletedDate", target = "deletedDate", dateFormat = CommonConstants.UTC_TIMESTAMP_PATTERN)
    @Mapping(source = "expiresDate", target = "expiresDate", dateFormat = CommonConstants.UTC_TIMESTAMP_PATTERN)
    @Named("toHierarchyResponse")
    OrganizationResponseDto toHierarchyResponse(Organization organization);

    /**
     * Maps list of Organization entities to hierarchy response list.
     */
    @IterableMapping(qualifiedByName = "toHierarchyResponse")
    List<OrganizationResponseDto> toHierarchyResponseList(List<Organization> organizations);

    @AfterMapping
    default void applyDefaults(@MappingTarget Organization organization) {
        organization.applyDefaults();
        
        if (organization.getSetting() != null) {
            OrganizationSetting setting = organization.getSetting();
            setting.applyDefaults();
        }
        if (organization.getDatabase() != null) {
            DatabaseConfig databaseConfig = organization.getDatabase();
            databaseConfig.applyDefaults();
        }
        if (organization.getMetadata() != null) {
            OrganizationMetadata organizationMetadata = organization.getMetadata();
            organizationMetadata.applyDefaults();
        }
    }
}