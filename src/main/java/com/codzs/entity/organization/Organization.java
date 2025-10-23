package com.codzs.entity.organization;

import com.codzs.constant.organization.OrganizationStatusEnum;
import com.codzs.entity.domain.Domain;
import com.codzs.framework.entity.BaseEntity;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

/**
 * MongoDB Document representing organizations within the Codzs Platform.
 * This entity stores organization information including settings, domains,
 * database configuration, and hierarchical relationships.
 * 
 * Storage Database: codzs_auth_{env}
 * Collection: organization
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Document(collection = "organization")
@Getter
@Setter
@NoArgsConstructor
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
    @Indexed
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
    private DatabaseConfig database;

    @Valid
    private OrganizationSettings settings;

    @Valid
    private OrganizationMetadata metadata;

    @Valid
    @Schema(description = "Organization domains")
    private List<Domain> domains;

    @NotEmpty(message = "At least one owner user ID is required")
    @Indexed
    private List<String> ownerUserIds;

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
    }

    // Initialize default values for default constructor
    @PostConstruct
    private void initDefaults() {
        if (this.status == null) {
            this.status = OrganizationStatusEnum.getDefault();
        }
    }

    // Utility methods
    @Override
    public void softDelete(String deletedBy) {
        this.status = OrganizationStatusEnum.DELETED;
        super.softDelete(deletedBy);
    }
}