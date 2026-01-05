package com.codzs.dto.organization.response;

import com.codzs.constant.organization.OrganizationSchemaConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for organization metadata in organization responses.
 * Contains additional categorization information for organizations.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = OrganizationSchemaConstants.ORG_METADATA_RESPONSE_DESCRIPTION)
public class OrganizationMetadataResponseDto {

    @Schema(description = OrganizationSchemaConstants.INDUSTRY_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_INDUSTRY)
    private String industry;

    @Schema(description = OrganizationSchemaConstants.SIZE_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_SIZE)
    private String size;

    // Custom constructor for convenience
    public OrganizationMetadataResponseDto(String industry, String size) {
        this.industry = industry;
        this.size = size;
    }
}