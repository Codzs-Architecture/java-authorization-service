package com.codzs.entity.organization;

import com.codzs.constant.organization.OrganizationIndustryEnum;
import com.codzs.constant.organization.OrganizationSizeEnum;
import com.codzs.framework.entity.EntityDefaultInitializer;
import com.codzs.framework.helper.SpringContextHelper;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Organization metadata sub-entity representing additional
 * categorization data for organizations.
 * Used as an embedded object within Organization entity.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrganizationMetadata {

    private String industry;

    private String size;

    // Initialize default values for default constructor
    @PostConstruct
    private void initDefaults() {
        OrganizationIndustryEnum organizationIndustryEnum = SpringContextHelper.getBean(OrganizationIndustryEnum.class);
        OrganizationSizeEnum organizationSizeEnum = SpringContextHelper.getBean(OrganizationSizeEnum.class);

        this.industry = EntityDefaultInitializer.setDefaultIfNull(this.industry, organizationIndustryEnum.getDefaultValue());
        this.size = EntityDefaultInitializer.setDefaultIfNull(this.size, organizationSizeEnum.getDefaultValue());
    }
}