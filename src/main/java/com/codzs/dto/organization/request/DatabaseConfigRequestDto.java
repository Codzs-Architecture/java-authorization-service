package com.codzs.dto.organization.request;

import com.codzs.constant.organization.OrganizationSwaggerConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * DTO for database configuration in organization requests.
 * Contains database connection and schema information.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Database configuration for organization")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class DatabaseConfigRequestDto {

    @NotBlank(message = "Connection string is required")
    @Schema(description = "Database connection string", 
            example = OrganizationSwaggerConstants.EXAMPLE_CONNECTION_STRING, 
            required = true)
    private String connectionString;

    @NotBlank(message = "Certificate is required")
    @Schema(description = "Database certificate", 
            example = OrganizationSwaggerConstants.EXAMPLE_CERTIFICATE, 
            required = true)
    private String certificate;

    @NotEmpty(message = "At least one schema is required")
    @Valid
    @Schema(description = "List of database schemas", required = true)
    private List<DatabaseSchemaRequestDto> schemas;

    public DatabaseConfigRequestDto(String connectionString, String certificate, List<DatabaseSchemaRequestDto> schemas) {
        this.connectionString = connectionString;
        this.certificate = certificate;
        this.schemas = schemas;
    }
}