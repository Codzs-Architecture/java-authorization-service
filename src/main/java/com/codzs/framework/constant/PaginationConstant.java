package com.codzs.framework.constant;

/**
 * Constants for Organization module.
 * Contains all static values, default configurations, and business rules
 * related to organization management.
 * 
 * @author Codzs Team
 * @since 1.0
 */
public final class PaginationConstant {

    // private HeaderConstants() {
    //     // Utility class - prevent instantiation
    // }

    // ========== Pagination ==========
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    public static final int DEFAULT_PAGE_NUMBER = 1;
    public static final int DEFAULT_AUTOCOMPLETE_LIMIT = 10;
    public static final String DEFAULT_SORT_BY = "createdDate";
    public static final String DEFAULT_SORT_ORDER = "asc";
    
    public static final String DEFAULT_PAGE_SIZE_STR = DEFAULT_PAGE_SIZE + "";
    public static final String MAX_PAGE_SIZE_STR = MAX_PAGE_SIZE + "";
    public static final String DEFAULT_PAGE_NUMBER_STR = DEFAULT_PAGE_NUMBER + "";
    public static final String DEFAULT_AUTOCOMPLETE_LIMIT_STR = DEFAULT_AUTOCOMPLETE_LIMIT + "";

}