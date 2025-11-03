package com.codzs.dto.organization.response;

import com.codzs.constant.organization.OrganizationSchemaConstants;
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
@Schema(description = OrganizationSchemaConstants.DATABASE_CONFIG_RESPONSE_DESCRIPTION)
public class DatabaseConfigResponseDto {

    @Schema(description = OrganizationSchemaConstants.CONNECTION_STRING_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_CONNECTION_STRING)
    private String connectionString;

    @Schema(description = OrganizationSchemaConstants.CERTIFICATE_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_CERTIFICATE)
    private String certificate;

    @Schema(description = OrganizationSchemaConstants.SCHEMAS_LIST_DESCRIPTION)
    private List<DatabaseSchemaResponseDto> schemas;

    @Schema(description = OrganizationSchemaConstants.TEST_RESULTS_DESCRIPTION)
    private boolean connectionTestResults;

    // Custom constructor for convenience
    public DatabaseConfigResponseDto(String connectionString, String certificate, List<DatabaseSchemaResponseDto> schemas) {
        this.connectionString = connectionString;
        this.certificate = certificate;
        this.schemas = schemas;
    }
}