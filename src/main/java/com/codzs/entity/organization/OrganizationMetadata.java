package com.codzs.entity.organization;

import org.mapstruct.AfterMapping;

import com.codzs.constant.organization.OrganizationIndustryEnum;
import com.codzs.constant.organization.OrganizationSizeEnum;
import com.codzs.framework.context.spring.SpringContextHelper;
import com.codzs.framework.util.StringUtil;

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
@AllArgsConstructor
@ToString
public class OrganizationMetadata {

    private String industry;

    private String size;

    public OrganizationMetadata()   {
        this.applyDefaults();
    }

    @AfterMapping
    public void applyDefaults() {
        OrganizationIndustryEnum organizationIndustryEnum = SpringContextHelper.getBean(OrganizationIndustryEnum.class);
        OrganizationSizeEnum organizationSizeEnum = SpringContextHelper.getBean(OrganizationSizeEnum.class);

        this.industry = StringUtil.setDefaultIfNull(this.industry, organizationIndustryEnum.getDefaultValue());
        this.size = StringUtil.setDefaultIfNull(this.size, organizationSizeEnum.getDefaultValue());
    }
}