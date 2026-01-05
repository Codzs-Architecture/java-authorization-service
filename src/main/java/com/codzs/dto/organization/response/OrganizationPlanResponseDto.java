package com.codzs.dto.organization.response;

import com.codzs.constant.organization.OrganizationSchemaConstants;
import com.codzs.framework.constant.CommonConstants;
import com.codzs.framework.dto.BaseDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

/**
 * DTO for organization plan association responses.
 * Contains complete organization plan information with audit fields.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = OrganizationSchemaConstants.ORG_PLAN_RESPONSE_DESCRIPTION)
public class OrganizationPlanResponseDto extends BaseDto {

    @Schema(description = OrganizationSchemaConstants.ORG_PLAN_ID_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_ORG_PLAN_ID)
    private String id;

    @Schema(description = OrganizationSchemaConstants.ORG_ID_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_ID)
    private String organizationId;

    @Schema(description = OrganizationSchemaConstants.PLAN_ID_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_PLAN_ID)
    private String planId;

    @Schema(description = OrganizationSchemaConstants.PLAN_COMMENT_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_PLAN_COMMENT)
    private String comment;

    @Schema(description = OrganizationSchemaConstants.PLAN_VALID_FROM_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_START_FROM)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.UTC_TIMESTAMP_PATTERN)
    private Instant validFrom;

    @Schema(description = OrganizationSchemaConstants.PLAN_VALID_TO_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_END_TO)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.UTC_TIMESTAMP_PATTERN)
    private Instant validTo;

    @Schema(description = OrganizationSchemaConstants.PLAN_ACTIVE_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_PLAN_ACTIVE)
    private Boolean isActive;
}