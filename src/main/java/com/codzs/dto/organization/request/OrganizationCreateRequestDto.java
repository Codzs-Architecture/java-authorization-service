package com.codzs.dto.organization.request;

import com.codzs.constant.organization.OrganizationSwaggerConstants;
import com.codzs.constant.organization.OrganizationTypeEnum;
import com.codzs.entity.security.User;
import com.codzs.entity.organization.Organization;
import com.codzs.framework.annotation.validation.ValidDynamicEnum;
import com.codzs.framework.annotation.validation.ValidEntityId;
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
@Schema(description = "Request DTO for creating a new organization")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class OrganizationCreateRequestDto {

    @NotBlank(message = "Organization name is required")
    @Size(min = 2, max = 100, message = "Organization name must be between 2 and 100 characters")
    @Schema(description = "Organization name", 
            example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_NAME, 
            required = true)
    private String name;

    @NotBlank(message = "Organization abbreviation is required")
    @Size(min = 2, max = 10, message = "Organization abbreviation must be between 2 and 10 characters")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Organization abbreviation must contain only uppercase alphanumeric characters")
    @Schema(description = "Organization abbreviation", 
            example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_ABBR, 
            required = true)
    private String abbr;

    @NotBlank(message = "Display name is required")
    @Size(min = 2, max = 255, message = "Display name must be between 2 and 255 characters")
    @Schema(description = "Organization display name", 
            example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_DISPLAY_NAME, 
            required = true)
    private String displayName;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Schema(description = "Organization description", 
            example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_DESCRIPTION)
    private String description;

    @NotBlank(message = "Organization type is required")
    @ValidDynamicEnum(enumClass = OrganizationTypeEnum.class, message = "Invalid organization type")
    @Schema(description = "Organization type", example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_TYPE, required = true, allowableValues = {"ENTERPRISE", "STANDARD", "STARTUP", "INDIVIDUAL"})
    private String organizationType;

    @NotBlank(message = "Billing email is required")
    @Email(message = "Billing email must be a valid email format")
    @Size(max = 255, message = "Billing email must not exceed 255 characters")
    @Schema(description = "Primary billing contact email", 
            example = OrganizationSwaggerConstants.EXAMPLE_BILLING_EMAIL, 
            required = true)
    private String billingEmail;

    @Schema(description = "Organization expiration date", 
            example = OrganizationSwaggerConstants.EXAMPLE_EXPIRY_DATE)
    private Instant expiresDate;

    @NotNull(message = "Database configuration is required")
    @Valid
    @Schema(description = "Database configuration for the organization", required = true)
    private DatabaseConfigRequestDto database;

    @Valid
    @Schema(description = "Organization settings")
    private OrganizationSettingsRequestDto settings;

    @Valid
    @Schema(description = "Organization metadata")
    private OrganizationMetadataRequestDto metadata;

    @Valid
    @Schema(description = "Organization domains")
    private List<DomainRequestDto> domains;

    @NotEmpty(message = "At least one owner user ID is required")
    @ValidEntityId(entityClass = User.class, checkDeleted = ValidEntityId.CheckDeletedStatus.NON_DELETED,
                   message = "Invalid user ID(s). One or more referenced users do not exist or are deleted.")
    @Schema(description = "List of owner user IDs", 
            example = OrganizationSwaggerConstants.EXAMPLE_USER_ID_LIST, 
            required = true)
    private List<String> ownerUserIds;

    @ValidEntityId(entityClass = Organization.class, allowNull = true, checkDeleted = ValidEntityId.CheckDeletedStatus.NON_DELETED,
                   message = "Invalid organization ID. The referenced organization does not exist or is deleted.")
    @Schema(description = "Parent organization ID", 
            example = OrganizationSwaggerConstants.EXAMPLE_PARENT_ORG_ID)
    private String parentOrganizationId;
}