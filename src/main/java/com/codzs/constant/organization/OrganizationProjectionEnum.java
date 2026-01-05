package com.codzs.constant.organization;

/**
 * Enum defining organization projection types for partial data retrieval.
 * Used to specify which parts of organization data should be included in responses.
 * 
 * @author Codzs Team
 * @since 1.0
 */
public enum OrganizationProjectionEnum {
    
    SETTING("setting"),
    DOMAIN("domain"),
    METADATA("metadata"),
    DATABASE("database");
    
    private final String value;
    
    OrganizationProjectionEnum(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static final String ORGANIZATION_SETTING = "setting";
    public static final String ORGANIZATION_DOMAIN = "domain";
    public static final String ORGANIZATION_METADATA = "metadata";
    public static final String ORGANIZATION_DATABASE = "database";
}