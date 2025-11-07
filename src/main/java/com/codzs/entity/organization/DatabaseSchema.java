package com.codzs.entity.organization;

import org.springframework.data.annotation.Id;

import com.codzs.framework.entity.BaseEntity;
import com.codzs.util.domain.DomainUtil;
import com.codzs.util.organization.OrganizationDatabaseConfigUtil;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Database schema sub-entity representing individual schemas
 * for different services within an organization.
 * Used as an embedded object within DatabaseConfig.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class DatabaseSchema extends BaseEntity {

    @Id
    private String id;

    @NotBlank(message = "Service type is required")
    private String forService;

    @NotBlank(message = "Schema name is required")
    private String schemaName;

    @NotBlank(message = "Description is required")
    private String description;

    // Custom constructor with parameters
    public DatabaseSchema(String forService, String schemaName, String description) {
        this.forService = forService;
        this.schemaName = schemaName;
        this.description = description;
    }

    public void setSchemaName(String schemaName) {
      this.schemaName = OrganizationDatabaseConfigUtil.normalizeSchemaName(schemaName);
    }
}