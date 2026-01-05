package com.codzs.dto.organization.request;

import com.codzs.constant.organization.OrganizationSchemaConstants;
import com.codzs.constant.organization.OrganizationIndustryEnum;
import com.codzs.constant.organization.OrganizationSizeEnum;
import com.codzs.framework.validation.annotation.ValidDynamicEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for organization metadata in organization requests.
 * Contains additional categorization information for organizations.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = OrganizationSchemaConstants.ORG_METADATA_DESCRIPTION)
@Getter
@Setter
@NoArgsConstructor
@ToString
public class OrganizationMetadataRequestDto {

    @ValidDynamicEnum(enumClass = OrganizationIndustryEnum.class, allowNull = true, message = OrganizationSchemaConstants.INDUSTRY_INVALID_MESSAGE)
    @Schema(description = OrganizationSchemaConstants.INDUSTRY_DESCRIPTION, 
            example = OrganizationSchemaConstants.EXAMPLE_INDUSTRY, 
            allowableValues = {"TECHNOLOGY", "FINANCE", "HEALTHCARE", "EDUCATION", "RETAIL", "MANUFACTURING", "CONSULTING", "MEDIA", "NONPROFIT", "GOVERNMENT", "REAL_ESTATE", "CONSTRUCTION", "TRANSPORTATION", "ENERGY", "TELECOMMUNICATIONS", "OTHER"})
    private String industry;

    @ValidDynamicEnum(enumClass = OrganizationSizeEnum.class, allowNull = true, message = OrganizationSchemaConstants.SIZE_INVALID_MESSAGE)
    @Schema(description = OrganizationSchemaConstants.SIZE_DESCRIPTION, 
            example = OrganizationSchemaConstants.EXAMPLE_SIZE, 
            allowableValues = {"1-10", "11-200", "201-500", "500+"})
    private String size;

    public OrganizationMetadataRequestDto(String industry, String size) {
        this.industry = industry;
        this.size = size;
    }
}