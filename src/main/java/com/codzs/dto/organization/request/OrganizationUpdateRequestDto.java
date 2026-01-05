package com.codzs.dto.organization.request;

import com.codzs.constant.organization.OrganizationSchemaConstants;
import com.codzs.constant.organization.OrganizationTypeEnum;
import com.codzs.entity.security.User;
import com.codzs.framework.validation.annotation.ValidDynamicEnum;
import com.codzs.framework.validation.annotation.ValidEntityId;
import com.codzs.entity.organization.Organization;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.List;

/**
 * DTO for updating an existing organization.
 * All fields are optional for partial updates (PATCH operations).
 * 
 * @author Codzs Team
 * @since 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = OrganizationSchemaConstants.ORG_UPDATE_REQUEST_DESCRIPTION)
@Getter
@Setter
@NoArgsConstructor
@ToString
public class OrganizationUpdateRequestDto {

    @Size(min = 2, max = OrganizationSchemaConstants.MAX_NAME_LENGTH, message = OrganizationSchemaConstants.ORG_NAME_SIZE_MESSAGE)
    @Schema(description = OrganizationSchemaConstants.ORG_NAME_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_NAME)
    private String name;

    @Size(min = 2, max = OrganizationSchemaConstants.MAX_ABBR_LENGTH, message = OrganizationSchemaConstants.ORG_ABBR_SIZE_MESSAGE)
    @Pattern(regexp = OrganizationSchemaConstants.ORG_ABBR_PATTERN, message = OrganizationSchemaConstants.ORG_ABBR_PATTERN_MESSAGE)
    @Schema(description = OrganizationSchemaConstants.ORG_ABBR_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_ABBR)
    private String abbr;

    @Size(min = 2, max = OrganizationSchemaConstants.MAX_DISPLAY_NAME_LENGTH, message = OrganizationSchemaConstants.DISPLAY_NAME_SIZE_MESSAGE)
    @Schema(description = OrganizationSchemaConstants.ORG_DISPLAY_NAME_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_DISPLAY_NAME)
    private String displayName;

    @Size(max = OrganizationSchemaConstants.MAX_DESCRIPTION_LENGTH, message = OrganizationSchemaConstants.DESCRIPTION_SIZE_MESSAGE)
    @Schema(description = OrganizationSchemaConstants.ORG_DESCRIPTION_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_DESCRIPTION)
    private String description;

    @ValidDynamicEnum(enumClass = OrganizationTypeEnum.class, allowNull = true, message = OrganizationSchemaConstants.ORG_TYPE_INVALID_MESSAGE)
    @Schema(description = OrganizationSchemaConstants.ORG_TYPE_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_TYPE, allowableValues = {"ENTERPRISE", "STANDARD", "STARTUP", "INDIVIDUAL"})
    private String organizationType;

    @Email(message = OrganizationSchemaConstants.BILLING_EMAIL_FORMAT_MESSAGE)
    @Size(max = 255, message = OrganizationSchemaConstants.BILLING_EMAIL_SIZE_MESSAGE)
    @Schema(description = OrganizationSchemaConstants.BILLING_EMAIL_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_BILLING_EMAIL)
    private String billingEmail;

    @Schema(description = OrganizationSchemaConstants.EXPIRY_DATE_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_EXPIRY_DATE)
    private Instant expiresDate;

    @ValidEntityId(entityClass = User.class, allowNull = true, checkDeleted = ValidEntityId.CheckDeletedStatus.NON_DELETED,
                   message = OrganizationSchemaConstants.INVALID_USER_ENTITY_MESSAGE)
    @Schema(description = OrganizationSchemaConstants.OWNER_USER_IDS_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_USER_ID_LIST)
    private List<String> ownerUserIds;

    @ValidEntityId(entityClass = Organization.class, allowNull = true, checkDeleted = ValidEntityId.CheckDeletedStatus.NON_DELETED,
                   message = OrganizationSchemaConstants.INVALID_ORG_ENTITY_MESSAGE)
    @Schema(description = OrganizationSchemaConstants.PARENT_ORG_ID_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_PARENT_ORG_ID)
    private String parentOrganizationId;

}