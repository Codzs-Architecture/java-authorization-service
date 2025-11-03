package com.codzs.dto.organization.response;

import com.codzs.constant.organization.OrganizationSchemaConstants;
import com.codzs.constant.organization.OrganizationStatusEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for organization summary responses.
 * Contains minimal organization information for list views and autocomplete.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = OrganizationSchemaConstants.ORG_SUMMARY_RESPONSE_DESCRIPTION)
public class OrganizationSummaryResponseDto {

    @Schema(description = OrganizationSchemaConstants.ORG_ID_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_ID)
    private String id;

    @Schema(description = OrganizationSchemaConstants.ORG_NAME_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_NAME)
    private String name;

    @Schema(description = OrganizationSchemaConstants.ORG_ABBR_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_ABBR)
    private String abbr;

    @Schema(description = OrganizationSchemaConstants.ORG_DISPLAY_NAME_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_DISPLAY_NAME)
    private String displayName;

    @Schema(description = OrganizationSchemaConstants.STATUS_DESCRIPTION, example = "ACTIVE")
    private OrganizationStatusEnum status;

    @Schema(description = OrganizationSchemaConstants.ORG_TYPE_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_TYPE)
    private String organizationType;

    @Schema(description = OrganizationSchemaConstants.BILLING_EMAIL_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_BILLING_EMAIL)
    private String billingEmail;

    @Schema(description = OrganizationSchemaConstants.PARENT_ORG_ID_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_PARENT_ORG_ID)
    private String parentOrganizationId;

    // Custom constructor for convenience
    public OrganizationSummaryResponseDto(String id, String name, String abbr, String displayName, 
                                         OrganizationStatusEnum status, String organizationType) {
        this.id = id;
        this.name = name;
        this.abbr = abbr;
        this.displayName = displayName;
        this.status = status;
        this.organizationType = organizationType;
    }
}