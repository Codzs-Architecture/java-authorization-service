package com.codzs.dto.organization.response;

import com.codzs.constant.organization.OrganizationSchemaConstants;
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
@Schema(description = OrganizationSchemaConstants.DATABASE_SCHEMA_RESPONSE_DESCRIPTION)
public class DatabaseSchemaResponseDto extends BaseDto {

    @Schema(description = OrganizationSchemaConstants.SCHEMA_ID_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_SCHEMA_ID)
    private String id;

    @Schema(description = OrganizationSchemaConstants.SERVICE_TYPE_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_SERVICE_TYPE)
    private String forService;

    @Schema(description = OrganizationSchemaConstants.SCHEMA_NAME_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_SCHEMA_NAME)
    private String schemaName;

    @Schema(description = OrganizationSchemaConstants.SCHEMA_DESCRIPTION_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_SCHEMA_DESCRIPTION)
    private String description;
}