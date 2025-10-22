package com.codzs.dto.organization.request;

import com.codzs.constant.organization.OrganizationSwaggerConstants;
import com.codzs.framework.annotation.validation.ValidEntityId;
import com.codzs.framework.constant.CommonConstants;
import com.codzs.entity.organization.Organization;
import com.codzs.entity.plan.Plan;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

/**
 * DTO for organization plan association requests.
 * Used for creating and updating organization plan associations.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@Schema(description = "Organization plan association request")
public class OrganizationPlanRequestDto {

    @Schema(description = "Organization ID", example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_ID, required = true)
    @NotBlank(message = "Organization ID is required")
    @ValidEntityId(entityClass = Organization.class, checkDeleted = ValidEntityId.CheckDeletedStatus.NON_DELETED,
                   message = "Invalid organization ID. The referenced organization does not exist or is deleted.")
    private String organizationId;

    @Schema(description = "Plan ID", example = OrganizationSwaggerConstants.EXAMPLE_PLAN_ID, required = true)
    @NotBlank(message = "Plan ID is required")
    @ValidEntityId(entityClass = Plan.class, checkDeleted = ValidEntityId.CheckDeletedStatus.NON_DELETED,
                   message = "Invalid plan ID. The referenced plan does not exist or is deleted.")
    private String planId;

    @Schema(description = "Comment for plan association", example = OrganizationSwaggerConstants.EXAMPLE_PLAN_COMMENT)
    @NotBlank(message = "Comment is required")
    @Size(max = 1000, message = "Comment must not exceed 1000 characters")
    private String comment;

    @Schema(description = "Plan valid from timestamp", example = OrganizationSwaggerConstants.EXAMPLE_PLAN_VALID_FROM)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.UTC_TIMESTAMP_PATTERN)
    private Instant validFrom;

    @Schema(description = "Plan valid to timestamp", example = OrganizationSwaggerConstants.EXAMPLE_PLAN_VALID_TO)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.UTC_TIMESTAMP_PATTERN)
    private Instant validTo;

    @Schema(description = "Whether this plan association is active", example = "true")
    private Boolean isActive;
}