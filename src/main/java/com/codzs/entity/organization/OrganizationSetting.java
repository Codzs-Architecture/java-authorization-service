package com.codzs.entity.organization;

import org.mapstruct.AfterMapping;

import com.codzs.framework.constant.CommonConstants;
import com.codzs.framework.util.StringUtil;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Organization setting sub-entity representing configuration
 * setting for organizations.
 * Used as an embedded object within Organization entity.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
public class OrganizationSetting {

    @Size(max = 50, message = "Language must not exceed 50 characters")
    private String language;

    @Size(max = 50, message = "Timezone must not exceed 50 characters")
    private String timezone;

    @Size(max = 10, message = "Currency must not exceed 10 characters")
    private String currency;

    @Size(max = 10, message = "Country must not exceed 10 characters")
    private String country;

    public OrganizationSetting() {
        this.applyDefaults();
    }
    
    // Initialize default values before persisting to database
    @AfterMapping
    public void applyDefaults() {
        this.language = StringUtil.setDefaultIfNull(this.language, CommonConstants.DEFAULT_LANGUAGE);
        this.timezone = StringUtil.setDefaultIfNull(this.timezone, CommonConstants.DEFAULT_TIMEZONE);
        this.currency = StringUtil.setDefaultIfNull(this.currency, CommonConstants.DEFAULT_CURRENCY);
        this.country = StringUtil.setDefaultIfNull(this.country, CommonConstants.DEFAULT_COUNTRY);
    }
}