// package com.codzs.validation.domain;

// import com.codzs.entity.domain.Domain;
// import com.codzs.exception.validation.ValidationException;
// import com.codzs.service.domain.DomainService;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Component;
// import org.springframework.util.StringUtils;

// import java.util.List;
// import java.util.ArrayList;

// /**
//  * Business validator for embedded Domain operations within <entity>.
//  * Focuses on domain business rules, verification logic, and <entity> constraints.
//  * Works with domains as embedded sub-objects rather than separate entities.
//  * 
//  * @author Codzs Team
//  * @since 1.0
//  */
// @Component
// public abstract class DomainBusinessValidator {

//     private final DomainService<?> domainService;

//     @Autowired
//     public DomainBusinessValidator(DomainService<?> domainService) {
//         this.domainService = domainService;
//     }

//     // ========== ENTRY POINT METHODS FOR <entity> APIs ==========

//     /**
//      * Validates domain addition to existing <entity>.
//      * Entry point for: POST /api/v1/<entity>/{id}/domains
//      */
//     public void validateDomainAdditionForEntity(List<Domain> domains, Domain domain, int maxCount,
//                                                      List<ValidationException.ValidationError> errors) {
//         validateDomainCountLimit(domains, maxCount, errors);
//         validateDomainForCreation(domain, -1, errors);
//         validatePrimaryDomainConstraint(domains, domain, errors);
//     }

//     /**
//      * Validates domain update request.
//      * Entry point for: PUT /api/v1/<entity>/{id}/domains/{domainId}
//      */
//     protected void validateDomainUpdate(Domain existingDomain, Domain updatedDomain) {
//         List<ValidationException.ValidationError> errors = new ArrayList<>();
        
//         // Check if domain name is being changed to an existing one
//         if (!existingDomain.getName().equals(updatedDomain.getName())) {
//             validateDomainGlobalUniqueness(updatedDomain.getName(), "", errors);
//         }
        
//         validateDomainNameFormat(updatedDomain.getName(), "", errors);
        
//         if (!errors.isEmpty()) {
//             throw new ValidationException("Domain update validation failed", errors);
//         }
//     }

//     /**
//      * Validates setting primary domain request.
//      * Entry point for: PUT /api/v1/<entity>/{id}/domains/{domainId}/primary
//      */
//     protected void validateSetPrimaryDomain(Domain domain) {
//         List<ValidationException.ValidationError> errors = new ArrayList<>();
        
//         if (!domain.getIsVerified()) {
//             errors.add(new ValidationException.ValidationError("domainId", 
//                 "Only verified domains can be set as primary"));
//         }
        
//         if (!errors.isEmpty()) {
//             throw new ValidationException("Set primary domain validation failed", errors);
//         }
//     }

//     /**
//      * Validates domain verification request.
//      * Entry point for: PUT /api/v1/<entity>/{id}/domains/{domainId}/verify
//      */
//     protected void validateDomainVerificationRequest(Domain domain, 
//                                                 String verificationMethod, String verificationToken) {
//         List<ValidationException.ValidationError> errors = new ArrayList<>();
        
//         validateDomainVerificationRules(domain, verificationMethod, errors);
        
//         // Additional validation for verification token
//         if (!domainService.validateVerificationToken(domain, verificationToken, verificationMethod)) {
//             errors.add(new ValidationException.ValidationError("verificationToken", "Invalid verification token"));
//         }
        
//         if (!errors.isEmpty()) {
//             throw new ValidationException("Domain verification validation failed", errors);
//         }
//     }

//     /**
//      * Validates domain verification request.
//      * Entry point for: PUT /api/v1/<entity>/{id}/domains/{domainId}/verify
//      */
//     public void validateDomainVerification(List<Domain> domains, String domainId, 
//                                          String verificationMethod, List<ValidationException.ValidationError> errors) {
//         Domain domain = findDomainInEntity(domains, domainId, errors);
//         if (domain == null) {
//             return;
//         }

//         validateDomainVerificationRules(domain, verificationMethod, errors);
//     }

//     /**
//      * Validates domains for <entity> creation.
//      */
//     public void validateDomainsForEntityCreation(List<Domain> domains, int maxCount,
//                                                       List<ValidationException.ValidationError> errors) {
//         if (domains == null || domains.isEmpty()) {
//             return;
//         }

//         // TODO: OrganizationConstants.MAX_DOMAINS_PER_ORGANIZATION
//         validateDomainCountLimit(domains, maxCount, errors);
//         validatePrimaryDomainUniqueness(domains, errors);
//         validateDomainDuplicates(domains, errors);
        
//         for (int i = 0; i < domains.size(); i++) {
//             validateDomainForCreation(domains.get(i), i, errors);
//         }
//     }

//     protected void validatePrimaryDomainConstraint(List<Domain> domains, Domain domain, 
//                                                List<ValidationException.ValidationError> errors) {
//         if (domain.getIsPrimary() != null && domain.getIsPrimary()) {
//             boolean hasExistingPrimary = domains != null && 
//                 domains.stream().anyMatch(Domain::getIsPrimary);
            
//             if (hasExistingPrimary) {
//                 errors.add(new ValidationException.ValidationError("isPrimary", 
//                     "We already has a primary domain. Only one primary domain is allowed."));
//             }
//         }
//     }

//     // ========== CORE VALIDATION METHODS ==========

//     protected void validateDomainCountLimit(List<Domain> domains, int maxCount, List<ValidationException.ValidationError> errors) {
//         int currentDomainCount = domains != null ? domains.size() : 0;

//         if (currentDomainCount > maxCount) {
//             errors.add(new ValidationException.ValidationError("domains", 
//                 "Cannot specify more than " + maxCount + " domains"));
//         }
//     }

//     protected void validatePrimaryDomainUniqueness(List<Domain> domains, 
//                                                 List<ValidationException.ValidationError> errors) {
//         long primaryDomainCount = domains != null ? domains.stream()
//             .filter(domain -> domain.getIsPrimary() != null && domain.getIsPrimary())
//             .count() : 0;
        
//         if (primaryDomainCount > 1) {
//             errors.add(new ValidationException.ValidationError("domains", 
//                 "Only one domain can be marked as primary"));
//         }
//     }

//     protected void validateDomainDuplicates(List<Domain> domains, 
//                                         List<ValidationException.ValidationError> errors) {
//         for (int i = 0; i < domains.size(); i++) {
//             String domainName = domains.get(i).getName();
//             for (int j = i + 1; j < domains.size(); j++) {
//                 if (domainName.equals(domains.get(j).getName())) {
//                     errors.add(new ValidationException.ValidationError("domains[" + j + "].name", 
//                         "Duplicate domain name: " + domainName));
//                 }
//             }
//         }
//     }

//     protected void validateDomainForCreation(Domain domain, int index, 
//                                          List<ValidationException.ValidationError> errors) {
//         String fieldPrefix = index >= 0 ? "domains[" + index + "]" : "";
        
//         validateDomainNameFormat(domain.getName(), fieldPrefix, errors);
//         validateDomainGlobalUniqueness(domain.getName(), fieldPrefix, errors);
//     }

//     protected void validateDomainNameFormat(String domainName, String fieldPrefix, 
//                                         List<ValidationException.ValidationError> errors) {
//         if (!StringUtils.hasText(domainName)) {
//             return;
//         }

//         // Only business logic validations remain here
//         validateReservedDomains(domainName, fieldPrefix, errors);
//     }

//     protected void validateReservedDomains(String domainName, String fieldPrefix, 
//                                        List<ValidationException.ValidationError> errors) {
//         String lowerDomain = domainName.toLowerCase();
        
//         if (lowerDomain.contains("localhost") || lowerDomain.contains("127.0.0.1") || 
//             lowerDomain.contains("0.0.0.0") || lowerDomain.endsWith(".local") ||
//             lowerDomain.contains("codzs.com") || lowerDomain.startsWith("api.") || 
//             lowerDomain.startsWith("admin.")) {
//             errors.add(new ValidationException.ValidationError(
//                 StringUtils.hasText(fieldPrefix) ? fieldPrefix + ".name" : "name", 
//                 "Cannot use reserved or platform domain names"));
//         }
//     }

//     protected void validateDomainGlobalUniqueness(String domainName, String fieldPrefix, 
//                                               List<ValidationException.ValidationError> errors) {
//         if (StringUtils.hasText(domainName) && domainService.isDomainAlreadyRegistered(domainName)) {
//             errors.add(new ValidationException.ValidationError(
//                 StringUtils.hasText(fieldPrefix) ? fieldPrefix + ".name" : "name", 
//                 "Domain is already registered"));
//         }
//     }

//     protected void validateDomainVerificationRules(Domain domain, String verificationMethod, 
//                                                 List<ValidationException.ValidationError> errors) {
//         if (domain.getIsVerified()) {
//             errors.add(new ValidationException.ValidationError("domainId", "Domain is already verified"));
//             return;
//         }

//         if (!domain.getVerificationMethod().equals(verificationMethod)) {
//             errors.add(new ValidationException.ValidationError("verificationMethod", 
//                 "Verification method does not match domain's configured method"));
//             return;
//         }

//         // Use service layer to check verification expiry
//         if (domainService.isVerificationExpired(domain)) {
//             errors.add(new ValidationException.ValidationError("domainId", 
//                 "Domain verification token has expired"));
//             return;
//         }

//         validateVerificationRequirements(domain, verificationMethod, errors);
//     }

//     protected void validateVerificationRequirements(Domain domain, String verificationMethod, 
//                                                  List<ValidationException.ValidationError> errors) {
//         // Use service layer to validate verification token
//         if (!domainService.isVerificationTokenValid(domain.getId(), domain.getVerificationToken())) {
//             errors.add(new ValidationException.ValidationError("verificationMethod", 
//                 "Invalid or missing verification token for " + verificationMethod + " verification"));
//         }
//     }

//     protected Domain findDomainInEntity(List<Domain> domains, String domainId, 
//                                           List<ValidationException.ValidationError> errors) {
//         // Domain ID required validation is handled by @NotBlank annotation in request DTOs
        
//         if (domains == null) {
//             errors.add(new ValidationException.ValidationError("domainId", "Domain not found"));
//             return null;
//         }

//         return domains.stream()
//             .filter(domain -> domainId.equals(domain.getId()))
//             .findFirst()
//             .orElseGet(() -> {
//                 errors.add(new ValidationException.ValidationError("domainId", "Domain not found"));
//                 return null;
//             });
//     }
// }