package com.codzs.entity.organization;

import com.codzs.constant.organization.OrganizationStatusEnum;
import com.codzs.constant.organization.OrganizationTypeEnum;
import com.codzs.entity.domain.Domain;
import com.codzs.framework.entity.BaseEntity;
import com.codzs.framework.entity.EntityDefaultInitializer;
import com.codzs.framework.helper.SpringContextHelper;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.mapstruct.AfterMapping;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

/**
 * MongoDB Document representing organizations within the Codzs Platform.
 * This entity stores organization information including setting, domains,
 * database configuration, and hierarchical relationships.
 * 
 * Storage Database: codzs_auth_{env}
 * Collection: organization
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Document(collection = "organization")
@CompoundIndexes({
    // Primary filtering index - Most critical for findWithFilters performance
    @CompoundIndex(name = "org_filter_idx", def = "{'deletedDate': 1, 'status': 1, 'organizationType': 1}"),
    
    // Hierarchy management index - Critical for parent-child queries
    @CompoundIndex(name = "org_hierarchy_idx", def = "{'parentOrganizationId': 1, 'deletedDate': 1, 'status': 1}"),
    
    // Search and status index - Critical for autocomplete functionality
    @CompoundIndex(name = "org_search_idx", def = "{'deletedDate': 1, 'status': 1, 'name': 1}"),
    
    // Metadata filtering index - Important for advanced filtering
    @CompoundIndex(name = "org_metadata_idx", def = "{'deletedDate': 1, 'metadata.industry': 1, 'metadata.size': 1}"),
    
    // Access control index - Important for user-based queries
    @CompoundIndex(name = "org_access_idx", def = "{'ownerUserIds': 1, 'deletedDate': 1}"),
    
    // Domain operations index - Important for domain validation
    @CompoundIndex(name = "org_domain_idx", def = "{'deletedDate': 1, 'domains.name': 1}"),
    
    // Audit and time-based index - Useful for sorting and reporting
    @CompoundIndex(name = "org_audit_idx", def = "{'deletedDate': 1, 'createdDate': -1}"),
    
    // Expiration management index - Useful for maintenance operations
    @CompoundIndex(name = "org_expiry_idx", def = "{'deletedDate': 1, 'expiresDate': 1, 'status': 1}")
})
@Getter
@Setter
@ToString(callSuper = true)
public class Organization extends BaseEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    @NotBlank(message = "Organization name is required")
    @Size(min = 2, max = 100, message = "Organization name must be between 2 and 100 characters")
    private String name;

    @Indexed(unique = true)
    @NotBlank(message = "Organization abbreviation is required")
    @Size(min = 2, max = 10, message = "Organization abbreviation must be between 2 and 10 characters")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Organization abbreviation must contain only uppercase alphanumeric characters")
    private String abbr;

    @NotBlank(message = "Display name is required")
    @Size(min = 2, max = 255, message = "Display name must be between 2 and 255 characters")
    private String displayName;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Organization status is required")
    @Indexed
    private OrganizationStatusEnum status;

    @NotBlank(message = "Organization type is required")
    private String organizationType;

    @NotBlank(message = "Billing email is required")
    @Email(message = "Billing email must be a valid email format")
    @Size(max = 255, message = "Billing email must not exceed 255 characters")
    @Indexed
    private String billingEmail;

    @Indexed
    private Instant expiresDate;

    @NotNull(message = "Database configuration is required")
    @Valid
    private DatabaseConfig database = new DatabaseConfig();

    @Valid
    private OrganizationSetting setting = new OrganizationSetting();

    @Valid
    private OrganizationMetadata metadata = new OrganizationMetadata();

    @Valid
    @Schema(description = "Organization domains")
    private List<Domain> domains = List.of();

    @NotEmpty(message = "At least one owner user ID is required")
    @Indexed
    private List<String> ownerUserIds = List.of();

    @Indexed
    private String parentOrganizationId;

    // Custom constructor with parameters
    public Organization(String name, String abbr, String displayName, String organizationType, 
                       String billingEmail, DatabaseConfig database, List<String> ownerUserIds, String createdBy) {
        this.name = name;
        this.abbr = abbr;
        this.displayName = displayName;
        this.organizationType = organizationType;
        this.billingEmail = billingEmail;
        this.database = database;
        this.ownerUserIds = ownerUserIds;
        this.status = OrganizationStatusEnum.getDefault();
        this.applyDefaults();
    }

    public Organization() {
        this.applyDefaults();
    }

    // Initialize default values for default constructor
    @AfterMapping
    public void applyDefaults() {
        OrganizationTypeEnum organizationTypeEnum = SpringContextHelper.getBean(OrganizationTypeEnum.class);

        this.status = EntityDefaultInitializer.setDefaultIfNull(this.status, OrganizationStatusEnum.getDefault());
        this.organizationType = EntityDefaultInitializer.setDefaultIfNull(this.organizationType, organizationTypeEnum.getDefaultValue());
        if (this.displayName == null && this.name != null) {
            this.displayName = this.name;
        }
    }

    // Utility methods
    @Override
    public void softDelete(String deletedBy) {
        this.status = OrganizationStatusEnum.DELETED;
        super.softDelete(deletedBy);
    }
}