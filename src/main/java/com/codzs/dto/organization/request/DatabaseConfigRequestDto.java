package com.codzs.dto.organization.request;

import com.codzs.constant.organization.OrganizationSwaggerConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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

    public DatabaseConfigRequestDto(String connectionString, String certificate) {
        this.connectionString = connectionString;
        this.certificate = certificate;
    }
}