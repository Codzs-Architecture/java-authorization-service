package com.codzs.dto.organization.response;

import com.codzs.constant.organization.OrganizationSwaggerConstants;
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
@Schema(description = "Organization plan association response")
public class OrganizationPlanResponseDto extends BaseDto {

    @Schema(description = "Organization plan association unique identifier", example = OrganizationSwaggerConstants.EXAMPLE_ORG_PLAN_ID)
    private String id;

    @Schema(description = "Organization ID", example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_ID)
    private String organizationId;

    @Schema(description = "Plan ID", example = OrganizationSwaggerConstants.EXAMPLE_PLAN_ID)
    private String planId;

    @Schema(description = "Comment for plan association", example = OrganizationSwaggerConstants.EXAMPLE_PLAN_COMMENT)
    private String comment;

    @Schema(description = "Plan valid from timestamp", example = OrganizationSwaggerConstants.EXAMPLE_PLAN_VALID_FROM)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.UTC_TIMESTAMP_PATTERN)
    private Instant validFrom;

    @Schema(description = "Plan valid to timestamp", example = OrganizationSwaggerConstants.EXAMPLE_PLAN_VALID_TO)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.UTC_TIMESTAMP_PATTERN)
    private Instant validTo;

    @Schema(description = "Whether this plan association is active", example = OrganizationSwaggerConstants.EXAMPLE_PLAN_ACTIVE)
    private Boolean isActive;
}