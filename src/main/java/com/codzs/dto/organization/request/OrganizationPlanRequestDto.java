package com.codzs.dto.organization.request;

import com.codzs.constant.organization.OrganizationSchemaConstants;
import com.codzs.framework.constant.CommonConstants;
import com.codzs.framework.validation.annotation.ValidEntityId;
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
@Schema(description = OrganizationSchemaConstants.ORG_PLAN_REQUEST_DESCRIPTION)
public class OrganizationPlanRequestDto {

    @Schema(description = OrganizationSchemaConstants.ORG_ID_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_ID, required = true)
    @NotBlank(message = OrganizationSchemaConstants.ORG_ID_REQUIRED_MESSAGE)
    @ValidEntityId(entityClass = Organization.class, checkDeleted = ValidEntityId.CheckDeletedStatus.NON_DELETED,
                   message = OrganizationSchemaConstants.INVALID_ORG_ENTITY_MESSAGE)
    private String organizationId;

    @Schema(description = OrganizationSchemaConstants.PLAN_ID_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_PLAN_ID, required = true)
    @NotBlank(message = OrganizationSchemaConstants.PLAN_ID_REQUIRED_MESSAGE)
    @ValidEntityId(entityClass = Plan.class, checkDeleted = ValidEntityId.CheckDeletedStatus.NON_DELETED,
                   message = OrganizationSchemaConstants.INVALID_PLAN_ENTITY_MESSAGE)
    private String planId;

    @Schema(description = OrganizationSchemaConstants.PLAN_COMMENT_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_PLAN_COMMENT)
    @NotBlank(message = OrganizationSchemaConstants.COMMENT_REQUIRED_MESSAGE)
    @Size(max = OrganizationSchemaConstants.MAX_PLAN_COMMENT_LENGTH, message = OrganizationSchemaConstants.COMMENT_SIZE_MESSAGE)
    private String comment;

    @Schema(description = OrganizationSchemaConstants.PLAN_VALID_FROM_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_START_FROM)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.UTC_TIMESTAMP_PATTERN)
    private Instant validFrom;

    @Schema(description = OrganizationSchemaConstants.PLAN_VALID_TO_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_END_TO)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.UTC_TIMESTAMP_PATTERN)
    private Instant validTo;

    @Schema(description = OrganizationSchemaConstants.PLAN_ACTIVE_DESCRIPTION, example = "true")
    private Boolean isActive;
}