package com.codzs.dto.organization.request;

import com.codzs.constant.organization.OrganizationSchemaConstants;
import com.codzs.framework.annotation.validation.ValidDynamicEnum;
import com.codzs.framework.constant.ServiceTypeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for database schema in organization requests.
 * Represents individual database schema configuration.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = OrganizationSchemaConstants.DATABASE_SCHEMA_DESCRIPTION)
@Getter
@Setter
@NoArgsConstructor
@ToString
public class DatabaseSchemaRequestDto {

    @NotBlank(message = OrganizationSchemaConstants.SERVICE_TYPE_REQUIRED_MESSAGE)
    @ValidDynamicEnum(enumClass = ServiceTypeEnum.class, message = OrganizationSchemaConstants.SERVICE_TYPE_INVALID_MESSAGE)
    @Schema(description = OrganizationSchemaConstants.SERVICE_TYPE_DESCRIPTION, 
            example = OrganizationSchemaConstants.EXAMPLE_SERVICE_TYPE, 
            required = true, 
            allowableValues = {"auth", "billing", "analytics", "audit", "resource", "bff"})
    private String forService;

    @NotBlank(message = OrganizationSchemaConstants.SCHEMA_NAME_REQUIRED_MESSAGE)
    @Size(min = OrganizationSchemaConstants.MIN_SCHEMA_NAME_LENGTH, 
          max = OrganizationSchemaConstants.MAX_SCHEMA_NAME_LENGTH, 
          message = OrganizationSchemaConstants.SCHEMA_NAME_SIZE_MESSAGE)
    @Pattern(regexp = OrganizationSchemaConstants.SCHEMA_NAME_PATTERN, 
             message = OrganizationSchemaConstants.SCHEMA_NAME_PATTERN_MESSAGE)
    @Schema(description = OrganizationSchemaConstants.SCHEMA_NAME_DESCRIPTION, 
            example = OrganizationSchemaConstants.EXAMPLE_SCHEMA_NAME, 
            required = true)
    private String schemaName;

    @Size(max = OrganizationSchemaConstants.MAX_DESCRIPTION_LENGTH, 
        message = OrganizationSchemaConstants.SCHEMA_DESCRIPTION_SIZE_MESSAGE)
    @Schema(description = OrganizationSchemaConstants.SCHEMA_DESCRIPTION_DESCRIPTION, 
            example = OrganizationSchemaConstants.EXAMPLE_SCHEMA_DESCRIPTION)
    private String description;

    public DatabaseSchemaRequestDto(String forService, String schemaName, String description) {
        this.forService = forService;
        this.schemaName = schemaName;
        this.description = description;
    }
}