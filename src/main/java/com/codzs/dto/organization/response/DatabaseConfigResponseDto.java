package com.codzs.dto.organization.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * DTO for database configuration in organization responses.
 * Contains database connection and schema information.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Database configuration response")
public class DatabaseConfigResponseDto {

    @Schema(description = "Database connection string", example = "mongodb://localhost:27017/codzs_acme_auth_dev")
    private String connectionString;

    @Schema(description = "Database certificate", example = "-----BEGIN CERTIFICATE-----...")
    private String certificate;

    @Schema(description = "List of database schemas")
    private List<DatabaseSchemaResponseDto> schemas;

    // Custom constructor for convenience
    public DatabaseConfigResponseDto(String connectionString, String certificate, List<DatabaseSchemaResponseDto> schemas) {
        this.connectionString = connectionString;
        this.certificate = certificate;
        this.schemas = schemas;
    }
}