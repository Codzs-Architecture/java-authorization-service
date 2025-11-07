// package com.codzs.framework.exception.example;

// import com.codzs.framework.exception.util.ExceptionUtils;
// import org.springframework.stereotype.Component;

// /**
//  * Examples of how to use standardized exceptions in controllers.
//  * This class demonstrates best practices for exception handling.
//  * 
//  * @author CodeGeneration Framework
//  * @since 1.0
//  */
// @Component
// public class ControllerExceptionExamples {

//     /**
//      * Example: Handle organization not found in controller
//      */
//     public void exampleOrganizationNotFound(String organizationId) {
//         // Instead of returning null or custom error handling
//         // Simply throw the standardized exception
//         throw ExceptionUtils.organizationNotFound(organizationId);
        
//         // This will automatically:
//         // 1. Return HTTP 404 status
//         // 2. Include multi-tenant context
//         // 3. Log with proper error ID
//         // 4. Return standardized error response
//     }

//     /**
//      * Example: Handle duplicate resource creation
//      */
//     public void exampleDuplicateOrganization(String organizationName) {
//         // Before creating, if duplicate is detected
//         throw ExceptionUtils.organizationNameExists(organizationName);
        
//         // This will automatically:
//         // 1. Return HTTP 409 status
//         // 2. Include proper error message
//         // 3. Follow standardized error format
//     }

//     /**
//      * Example: Handle invalid business operations
//      */
//     public void exampleInvalidDomainDeletion() {
//         // When trying to delete primary domain
//         throw ExceptionUtils.cannotDeletePrimaryDomain();
        
//         // This will automatically:
//         // 1. Return HTTP 422 status
//         // 2. Include business rule explanation
//         // 3. Maintain consistent error structure
//     }

//     /**
//      * Example: Service layer usage
//      */
//     public void serviceLayerExample(String organizationId) {
//         // In service layer, simply throw - no need to handle HTTP responses
//         if (organizationNotExists(organizationId)) {
//             throw ExceptionUtils.organizationNotFound(organizationId);
//         }
        
//         // Service continues with business logic
//         // Controller layer automatically handles the exception
//     }

//     /**
//      * Example: Controller method signature (clean and simple)
//      */
//     // @GetMapping("/{organizationId}")
//     // public ResponseEntity<OrganizationDto> getOrganization(@PathVariable String organizationId) {
//     //     // No try-catch needed!
//     //     // Just call service - exceptions are handled globally
//     //     Organization org = organizationService.findById(organizationId);
//     //     return ResponseEntity.ok(mapper.toDto(org));
//     // }

//     private boolean organizationNotExists(String organizationId) {
//         // Simulate check
//         return true;
//     }
// }