package com.codzs.framework.annotation.header;

import com.codzs.constant.organization.OrganizationSchemaConstants;
import com.codzs.framework.constant.HeaderConstant;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Composite annotation that provides Swagger documentation for common header parameters.
 * This annotation reduces documentation duplication by providing consistent parameter 
 * descriptions for the three standard headers used across controllers.
 * 
 * <p>This annotation only handles the Swagger documentation. You still need to include
 * the actual @RequestHeader parameters in your method signature:</p>
 * 
 * <pre>
 * {@code
 * @GetMapping
 * @CommonHeaders  // Provides Swagger documentation
 * @Operation(summary = "Get some data")
 * public ResponseEntity<SomeDto> getData(
 *     @PathVariable String id,
 *     // Still need these parameters in method signature:
 *     @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
 *     @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
 *     @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId
 * ) {
 *     // method implementation
 * }
 * }
 * </pre>
 * 
 * @author CodeGeneration Framework
 * @since 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Parameters({
    @Parameter(
        name = HeaderConstant.HEADER_ORGANIZATION_ID,
        description = "Organization context for multi-tenant support", 
        example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_ID,
        required = false
    ),
    @Parameter(
        name = HeaderConstant.HEADER_TENANT_ID,
        description = "Tenant context for multi-tenant support", 
        example = OrganizationSchemaConstants.EXAMPLE_TENANT_ID,
        required = false
    ),
    @Parameter(
        name = HeaderConstant.HEADER_CORRELATION_ID,
        description = "Correlation ID for request tracing", 
        example = OrganizationSchemaConstants.EXAMPLE_CORRELATION_ID,
        required = false
    )
})
public @interface CommonHeaders {
}