package com.codzs.dto.organization.request;

import com.codzs.constant.organization.OrganizationConstants;
import com.codzs.constant.organization.OrganizationSwaggerConstants;
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
@Schema(description = "Database schema configuration")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class DatabaseSchemaRequestDto {

    @NotBlank(message = OrganizationConstants.SERVICE_TYPE_REQUIRED_MESSAGE)
    @ValidDynamicEnum(enumClass = ServiceTypeEnum.class, message = "Invalid service type")
    @Schema(description = "Type of service this schema supports", 
            example = OrganizationSwaggerConstants.EXAMPLE_SERVICE_TYPE, 
            required = true, 
            allowableValues = {"auth", "billing", "analytics", "audit", "resource", "bff"})
    private String forService;

    @NotBlank(message = OrganizationConstants.SCHEMA_NAME_REQUIRED_MESSAGE)
    @Size(min = OrganizationConstants.MIN_SCHEMA_NAME_LENGTH, 
          max = OrganizationConstants.MAX_SCHEMA_NAME_LENGTH, 
          message = "Schema name must be between " + OrganizationConstants.MIN_SCHEMA_NAME_LENGTH + " and " + OrganizationConstants.MAX_SCHEMA_NAME_LENGTH + " characters")
    @Pattern(regexp = OrganizationConstants.SCHEMA_NAME_PATTERN, 
             message = OrganizationConstants.SCHEMA_NAME_PATTERN_MESSAGE)
    @Schema(description = "Database schema name", 
            example = OrganizationSwaggerConstants.EXAMPLE_SCHEMA_NAME, 
            required = true)
    private String schemaName;

    @Size(max = OrganizationConstants.MAX_DESCRIPTION_LENGTH, 
        message = "Description must not exceed 1000 characters")
    @Schema(description = "Schema description", 
            example = OrganizationSwaggerConstants.EXAMPLE_SCHEMA_DESCRIPTION)
    private String description;

    public DatabaseSchemaRequestDto(String forService, String schemaName, String description) {
        this.forService = forService;
        this.schemaName = schemaName;
        this.description = description;
    }
}