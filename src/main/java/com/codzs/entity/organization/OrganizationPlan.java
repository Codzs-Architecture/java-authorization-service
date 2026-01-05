package com.codzs.entity.organization;

import com.codzs.constant.organization.OrganizationConstants;
import com.codzs.framework.entity.BaseEntity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * MongoDB Document representing organization plan associations.
 * This entity manages the relationship between organizations and their subscription plans,
 * including validity periods and activation status.
 * 
 * Storage Database: codzs_auth_{env}
 * Collection: organization_plan
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Document(collection = "organization_plan")
@CompoundIndexes({
    @CompoundIndex(name = "org_plan_idx", def = "{'organizationId': 1, 'planId': 1}"),
    @CompoundIndex(name = "org_active_idx", def = "{'organizationId': 1, 'isActive': 1}"),
    @CompoundIndex(name = "active_validity_idx", def = "{'isActive': 1, 'validTo': 1}"),
    @CompoundIndex(name = "org_active_validity_idx", def = "{'organizationId': 1, 'isActive': 1, 'validTo': 1}")
})
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class OrganizationPlan extends BaseEntity {

    @Id
    private String id;

    @NotBlank(message = "Organization ID is required")
    @Indexed
    private String organizationId;

    @NotBlank(message = "Plan ID is required")
    @Indexed
    private String planId;

    @NotBlank(message = "Comment is required")
    @Size(max = 1000, message = "Comment must not exceed 1000 characters")
    private String comment;

    @NotNull(message = "Valid from timestamp is required")
    @Indexed
    private Instant validFrom;

    @Indexed
    private Instant validTo;

    @NotNull(message = "Active status is required")
    @Indexed
    private Boolean isActive;

    // Custom constructors
    public OrganizationPlan(String organizationId, String planId, String comment, String createdBy) {
        super(createdBy);
        this.organizationId = organizationId;
        this.planId = planId;
        this.comment = comment;
        this.isActive = OrganizationConstants.DEFAULT_ORGANIZATION_PLAN_IS_ACTIVE;
        this.validFrom = Instant.now();
    }

    public OrganizationPlan(String organizationId, String planId, String comment, 
                           Instant validFrom, Instant validTo, String createdBy) {
        this(organizationId, planId, comment, createdBy);
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    // Utility methods
    public void deactivate(String lastModifiedBy) {
        this.isActive = false;
    }

    public void activate(String lastModifiedBy) {
        this.isActive = true;
    }

    @Override
    public void softDelete(String deletedBy) {
        this.isActive = false;
        super.softDelete(deletedBy);
    }

    public boolean isExpired() {
        return validTo != null && Instant.now().isAfter(validTo);
    }

    public boolean isCurrentlyValid() {
        Instant now = Instant.now();
        boolean afterValidFrom = validFrom == null || !now.isBefore(validFrom);
        boolean beforeValidTo = validTo == null || !now.isAfter(validTo);
        return isActive && afterValidFrom && beforeValidTo && !isDeleted();
    }

}