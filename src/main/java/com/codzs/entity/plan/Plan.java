package com.codzs.entity.plan;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.codzs.framework.entity.BaseEntity;
import com.codzs.constant.plan.PlanConstants;

/**
 * MongoDB Document representing subscription plans within the Codzs Platform.
 * This entity stores plan information including type, validity period,
 * and activation status.
 * 
 * Storage Database: codzs_billing_{env}
 * Collection: plan
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Document(collection = "plan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Plan extends BaseEntity {

    @Id
    private String id;

    @NotBlank(message = "Plan name is required")
    @Size(max = PlanConstants.MAX_PLAN_NAME_LENGTH, message = "Plan name must not exceed " + PlanConstants.MAX_PLAN_NAME_LENGTH + " characters")
    @Indexed(unique = true)
    private String name;

    @Size(max = PlanConstants.MAX_PLAN_DESCRIPTION_LENGTH, message = "Description must not exceed " + PlanConstants.MAX_PLAN_DESCRIPTION_LENGTH + " characters")
    private String description;

    @NotBlank(message = "Plan type is required")
    @Indexed
    private String type;

    @NotNull(message = "Validity period is required")
    @Min(value = PlanConstants.MIN_VALIDITY_PERIOD_DAYS, message = "Validity period must be at least " + PlanConstants.MIN_VALIDITY_PERIOD_DAYS)
    @Max(value = PlanConstants.MAX_VALIDITY_PERIOD_DAYS, message = "Validity period must not exceed " + PlanConstants.MAX_VALIDITY_PERIOD_DAYS)
    private Integer validityPeriod;

    @NotBlank(message = "Validity period unit is required")
    private String validityPeriodUnit;

    @NotNull(message = "Active status is required")
    @Indexed
    private Boolean isActive;
    
    @Indexed
    private Boolean isDeprecated;
    
    @DecimalMin(value = PlanConstants.MIN_PLAN_PRICE, message = "Price must be non-negative")
    private Double price;
    
    @Min(value = PlanConstants.MIN_PLAN_MAX_USERS, message = "Max users must be at least " + PlanConstants.MIN_PLAN_MAX_USERS)
    private Integer maxUsers;
    
    @Min(value = PlanConstants.MIN_PLAN_MAX_TENANTS, message = "Max tenants must be at least " + PlanConstants.MIN_PLAN_MAX_TENANTS)
    private Integer maxTenants;
    
    @Min(value = PlanConstants.MIN_PLAN_STORAGE_LIMIT, message = "Storage limit must be non-negative")
    private Long storageLimit;
}