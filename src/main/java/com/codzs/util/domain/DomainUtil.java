package com.codzs.util.domain;

import com.codzs.framework.constant.CommonConstants;

public class DomainUtil {
  public static String normalizeDomainName(String domainName) {
    if (domainName == null || domainName.trim().isEmpty()) {
        return null;
    }

    String normalized = domainName.trim().toLowerCase();
      
    // Basic domain validation - remove protocol if present
    if (normalized.startsWith(CommonConstants.HTTP_PROTOCOL)) {
        normalized = normalized.substring(7);
    } else if (normalized.startsWith(CommonConstants.HTTPS_PROTOCOL)) {
        normalized = normalized.substring(8);
    }

    // Remove trailing slash
    if (normalized.endsWith("/")) {
        normalized = normalized.substring(0, normalized.length() - 1);
    }

    return normalized;
  }

}
