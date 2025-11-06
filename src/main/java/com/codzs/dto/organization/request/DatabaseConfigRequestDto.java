package com.codzs.dto.organization.request;

import com.codzs.constant.organization.OrganizationSchemaConstants;
import com.codzs.framework.annotation.validation.ApplyDefaults;
import com.codzs.framework.helper.SpringContextHelper;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.core.env.Environment;

/**
 * DTO for database configuration in organization requests.
 * Contains database connection and schema information.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = OrganizationSchemaConstants.DATABASE_CONFIG_DESCRIPTION)
@ApplyDefaults
@Getter
@Setter
@NoArgsConstructor
@ToString
public class DatabaseConfigRequestDto {

    @NotBlank(message = OrganizationSchemaConstants.CONNECTION_STRING_REQUIRED_MESSAGE)
    @Size(max = 1000, message = OrganizationSchemaConstants.CONNECTION_STRING_SIZE_MESSAGE)
    @Schema(description = OrganizationSchemaConstants.CONNECTION_STRING_DESCRIPTION, 
            example = OrganizationSchemaConstants.EXAMPLE_CONNECTION_STRING, 
            required = true)
    private String connectionString;

    @NotBlank(message = OrganizationSchemaConstants.CERTIFICATE_REQUIRED_MESSAGE)
    @Size(max = 5000, message = OrganizationSchemaConstants.CERTIFICATE_SIZE_MESSAGE)
    @Schema(description = OrganizationSchemaConstants.CERTIFICATE_DESCRIPTION, 
            example = OrganizationSchemaConstants.EXAMPLE_CERTIFICATE, 
            required = true)
    private String certificate;

    public DatabaseConfigRequestDto(String connectionString, String certificate) {
        this.connectionString = connectionString;
        this.certificate = certificate;
    }
}