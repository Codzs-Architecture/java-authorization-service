package com.codzs.dto.organization.request;

import com.codzs.constant.organization.OrganizationSchemaConstants;
import com.codzs.constant.organization.OrganizationTypeEnum;
import com.codzs.dto.domain.request.DomainRequestDto;
import com.codzs.entity.organization.Organization;
import com.codzs.framework.validation.annotation.ValidDynamicEnum;
import com.codzs.framework.validation.annotation.ValidEntityId;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.List;

/**
 * DTO for creating a new organization.
 * Contains all required fields for organization creation with proper validation.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = OrganizationSchemaConstants.ORG_CREATE_REQUEST_DESCRIPTION)
@Getter
@Setter
@NoArgsConstructor
@ToString
public class OrganizationCreateRequestDto {

    @NotBlank(message = OrganizationSchemaConstants.ORG_NAME_REQUIRED_MESSAGE)
    @Size(min = 2, max = OrganizationSchemaConstants.MAX_NAME_LENGTH, message = OrganizationSchemaConstants.ORG_NAME_SIZE_MESSAGE)
    @Schema(description = OrganizationSchemaConstants.ORG_NAME_DESCRIPTION, 
            example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_NAME, 
            required = true)
    private String name;

    @NotBlank(message = OrganizationSchemaConstants.ORG_ABBR_REQUIRED_MESSAGE)
    @Size(min = 2, max = OrganizationSchemaConstants.MAX_ABBR_LENGTH, message = OrganizationSchemaConstants.ORG_ABBR_SIZE_MESSAGE)
    @Pattern(regexp = OrganizationSchemaConstants.ORG_ABBR_PATTERN, message = OrganizationSchemaConstants.ORG_ABBR_PATTERN_MESSAGE)
    @Schema(description = OrganizationSchemaConstants.ORG_ABBR_DESCRIPTION, 
            example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_ABBR, 
            required = true)
    private String abbr;

//     @NotBlank(message = "Display name is required")
    @Size(min = 2, max = OrganizationSchemaConstants.MAX_DISPLAY_NAME_LENGTH, message = OrganizationSchemaConstants.DISPLAY_NAME_SIZE_MESSAGE)
    @Schema(description = OrganizationSchemaConstants.ORG_DISPLAY_NAME_DESCRIPTION, 
            example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_DISPLAY_NAME, 
            required = false)
    private String displayName;

    @Size(max = OrganizationSchemaConstants.MAX_DESCRIPTION_LENGTH, message = OrganizationSchemaConstants.DESCRIPTION_SIZE_MESSAGE)
    @Schema(description = OrganizationSchemaConstants.ORG_DESCRIPTION_DESCRIPTION, 
            example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_DESCRIPTION)
    private String description;

    @NotBlank(message = OrganizationSchemaConstants.ORG_TYPE_REQUIRED_MESSAGE)
    @ValidDynamicEnum(enumClass = OrganizationTypeEnum.class, message = OrganizationSchemaConstants.ORG_TYPE_INVALID_MESSAGE)
    @Schema(description = OrganizationSchemaConstants.ORG_TYPE_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_TYPE, required = true, allowableValues = {"ENTERPRISE", "STANDARD", "STARTUP", "INDIVIDUAL"})
    private String organizationType;

    @NotBlank(message = OrganizationSchemaConstants.BILLING_EMAIL_REQUIRED_MESSAGE)
    @Email(message = OrganizationSchemaConstants.BILLING_EMAIL_FORMAT_MESSAGE)
    @Size(max = 255, message = OrganizationSchemaConstants.BILLING_EMAIL_SIZE_MESSAGE)
    @Schema(description = OrganizationSchemaConstants.BILLING_EMAIL_DESCRIPTION, 
            example = OrganizationSchemaConstants.EXAMPLE_BILLING_EMAIL, 
            required = true)
    private String billingEmail;

    @Schema(description = OrganizationSchemaConstants.EXPIRY_DATE_DESCRIPTION, 
            example = OrganizationSchemaConstants.EXAMPLE_EXPIRY_DATE)
    private Instant expiresDate;

    @Valid
    @Schema(description = OrganizationSchemaConstants.DATABASE_CONFIG_DESCRIPTION, required = true)
    private DatabaseConfigRequestDto database;

    @Valid
    @Schema(description = OrganizationSchemaConstants.ORG_SETTINGS_DESCRIPTION)
    private OrganizationSettingRequestDto setting;

    @Valid
    @Schema(description = OrganizationSchemaConstants.ORG_METADATA_DESCRIPTION)
    private OrganizationMetadataRequestDto metadata;

    @Valid
    @Schema(description = OrganizationSchemaConstants.DOMAINS_RESPONSE_DESCRIPTION)
    private List<DomainRequestDto> domains;

//     @NotEmpty(message = "At least one owner user ID is required")
//     @ValidEntityId(entityClass = User.class, checkDeleted = ValidEntityId.CheckDeletedStatus.NON_DELETED,
//                    message = "Invalid user ID(s). One or more referenced users do not exist or are deleted.")
//     @Schema(description = "List of owner user IDs", 
//             example = OrganizationSwaggerConstants.EXAMPLE_USER_ID_LIST, 
//             required = true)
//     private List<String> ownerUserIds;

    @ValidEntityId(entityClass = Organization.class, allowNull = true, checkDeleted = ValidEntityId.CheckDeletedStatus.NON_DELETED,
                   message = OrganizationSchemaConstants.INVALID_ORG_ENTITY_MESSAGE)
    @Schema(description = OrganizationSchemaConstants.PARENT_ORG_ID_DESCRIPTION, 
            example = OrganizationSchemaConstants.EXAMPLE_PARENT_ORG_ID)
    private String parentOrganizationId;

//     public OrganizationSettingRequestDto getSetting()   {
//         if (this.setting == null) {
//             this.setting = new OrganizationSettingRequestDto();
//         }

//         return this.setting;
//     }
}