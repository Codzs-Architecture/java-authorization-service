package com.codzs.dto.organization.response;

import com.codzs.framework.dto.BaseDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for database schema in organization responses.
 * Represents individual database schema configuration with audit fields.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Database schema response")
public class DatabaseSchemaResponseDto extends BaseDto {

    @Schema(description = "Schema unique identifier", example = "550e8400-e29b-41d4-a716-446655440000")
    private String id;

    @Schema(description = "Type of service this schema supports", example = "auth")
    private String forService;

    @Schema(description = "Database schema name", example = "codzs_acme_auth_dev")
    private String schemaName;

    @Schema(description = "Schema description", example = "Authentication service schema for Acme organization")
    private String description;
}