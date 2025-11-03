package com.codzs.constant.domain;

/**
 * Constants for domain-related default values and configurations.
 * Used for maintaining consistency across DTOs, entities, and documentation.
 * 
 * @author Codzs Team
 * @since 1.0
 */
public final class DomainConstants {
        
    // ========================= BOOLEAN DEFAULTS =========================
    
    /**
     * Boolean default value for domain verification status.
     */
    public static final boolean DEFAULT_IS_VERIFIED = false;
    
    /**
     * Boolean default value for primary domain status.
     */
    public static final boolean DEFAULT_IS_PRIMARY = false;
    
    // ========================= CONSTRUCTOR =========================
    
    /**
     * Private constructor to prevent instantiation.
     */
    private DomainConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}