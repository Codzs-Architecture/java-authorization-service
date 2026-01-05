package com.codzs.framework.constant;

public final class CacheConstants {
  
    // ========================= CACHE TTL VALUES (seconds) =========================
    
    /**
     * Short cache TTL (5 minutes).
     */
    public static final long CACHE_TTL_SHORT = 300;
    
    /**
     * Medium cache TTL (30 minutes).
     */
    public static final long CACHE_TTL_MEDIUM = 1800;
    
    /**
     * Long cache TTL (1 hour).
     */
    public static final long CACHE_TTL_LONG = 3600;
    
    /**
     * Very long cache TTL (24 hours).
     */
    public static final long CACHE_TTL_VERY_LONG = 86400;

    private CacheConstants() {
        // Utility class - prevent instantiation
    }
}
