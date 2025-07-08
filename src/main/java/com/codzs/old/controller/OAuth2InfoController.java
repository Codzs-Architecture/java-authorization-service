// package com.codzs.controller;

// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.Parameter;
// import io.swagger.v3.oas.annotations.media.Content;
// import io.swagger.v3.oas.annotations.media.ExampleObject;
// import io.swagger.v3.oas.annotations.media.Schema;
// import io.swagger.v3.oas.annotations.responses.ApiResponse;
// import io.swagger.v3.oas.annotations.responses.ApiResponses;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.*;

// @RestController
// @RequestMapping("/management/api")
// @Tag(name = "OAuth2 Authorization Server", description = "OAuth2 Authorization Server endpoints and information")
// public class OAuth2InfoController {

//     @Value("${authorization.server.url:https://local.codzs.com:5003}")
//     private String authorizationServerUrl;

//     @Operation(
//         summary = "OAuth2 Server Configuration", 
//         description = "Returns complete OAuth2 authorization server configuration including all supported endpoints, grant types, and scopes"
//     )
//     @ApiResponses(value = {
//         @ApiResponse(responseCode = "200", description = "OAuth2 server configuration",
//             content = @Content(mediaType = "application/json", 
//                 examples = @ExampleObject(value = """
//                     {
//                       "issuer": "https://local.codzs.com:5003",
//                       "authorization_endpoint": "https://local.codzs.com:5003/oauth2/authorize",
//                       "token_endpoint": "https://local.codzs.com:5003/oauth2/token",
//                       "grant_types_supported": ["authorization_code", "client_credentials", "refresh_token"]
//                     }
//                     """)))
//     })
//     @GetMapping("/oauth2/info")
//     public ResponseEntity<Map<String, Object>> getOAuth2Info() {
//         Map<String, Object> info = new HashMap<>();
//         info.put("issuer", authorizationServerUrl);
//         info.put("authorization_endpoint", authorizationServerUrl + "/oauth2/authorize");
//         info.put("token_endpoint", authorizationServerUrl + "/oauth2/token");
//         info.put("jwks_uri", authorizationServerUrl + "/oauth2/jwks");
//         info.put("introspection_endpoint", authorizationServerUrl + "/oauth2/introspect");
//         info.put("revocation_endpoint", authorizationServerUrl + "/oauth2/revoke");
//         info.put("userinfo_endpoint", authorizationServerUrl + "/userinfo");
//         info.put("end_session_endpoint", authorizationServerUrl + "/connect/logout");
//         info.put("device_authorization_endpoint", authorizationServerUrl + "/oauth2/device_authorization");
        
//         // Supported grant types
//         info.put("grant_types_supported", Arrays.asList(
//             "authorization_code", 
//             "client_credentials", 
//             "refresh_token",
//             "urn:ietf:params:oauth:grant-type:device_code"
//         ));
        
//         // Supported response types
//         info.put("response_types_supported", Arrays.asList("code"));
        
//         // Supported scopes
//         info.put("scopes_supported", Arrays.asList("openid", "read", "write"));
        
//         // Supported token endpoint auth methods
//         info.put("token_endpoint_auth_methods_supported", Arrays.asList("client_secret_basic", "client_secret_post"));
        
//         // Additional capabilities
//         info.put("code_challenge_methods_supported", Arrays.asList("S256"));
        
//         return ResponseEntity.ok(info);
//     }

//     @Operation(
//         summary = "OAuth2 Endpoints Directory", 
//         description = "Returns a comprehensive list of all available OAuth2 and OpenID Connect endpoints with their HTTP methods and descriptions"
//     )
//     @ApiResponses(value = {
//         @ApiResponse(responseCode = "200", description = "List of OAuth2 endpoints")
//     })
//     @GetMapping("/oauth2/endpoints")
//     public ResponseEntity<Map<String, Object>> getOAuth2Endpoints() {
//         Map<String, Object> response = new HashMap<>();
        
//         List<Map<String, String>> endpoints = new ArrayList<>();
        
//         endpoints.add(createEndpoint(
//             "Authorization Endpoint",
//             "GET",
//             "/oauth2/authorize",
//             "Initiates OAuth2 authorization code flow. Redirects user to login if not authenticated.",
//             "?response_type=code&client_id=CLIENT_ID&redirect_uri=REDIRECT_URI&scope=read&state=STATE"
//         ));
        
//         endpoints.add(createEndpoint(
//             "Token Endpoint",
//             "POST",
//             "/oauth2/token",
//             "Exchange authorization code for access token, or refresh existing token.",
//             "Content-Type: application/x-www-form-urlencoded"
//         ));
        
//         endpoints.add(createEndpoint(
//             "JWK Set Endpoint",
//             "GET",
//             "/oauth2/jwks",
//             "JSON Web Key Set containing public keys for token verification.",
//             ""
//         ));
        
//         endpoints.add(createEndpoint(
//             "Token Introspection",
//             "POST",
//             "/oauth2/introspect",
//             "Get information about an access token (RFC 7662).",
//             "Requires client authentication"
//         ));
        
//         endpoints.add(createEndpoint(
//             "Token Revocation",
//             "POST",
//             "/oauth2/revoke",
//             "Revoke an access or refresh token (RFC 7009).",
//             "Requires client authentication"
//         ));
        
//         endpoints.add(createEndpoint(
//             "Device Authorization",
//             "POST",
//             "/oauth2/device_authorization",
//             "Initiate device authorization flow (RFC 8628).",
//             "For devices with limited input capabilities"
//         ));
        
//         endpoints.add(createEndpoint(
//             "OpenID Connect Discovery",
//             "GET",
//             "/.well-known/openid-configuration",
//             "OpenID Connect discovery document with server metadata.",
//             ""
//         ));
        
//         endpoints.add(createEndpoint(
//             "User Info Endpoint",
//             "GET",
//             "/userinfo",
//             "Get user information using access token (OpenID Connect).",
//             "Requires Bearer token in Authorization header"
//         ));
        
//         endpoints.add(createEndpoint(
//             "Logout Endpoint",
//             "POST",
//             "/connect/logout",
//             "End user session and optionally redirect.",
//             "OpenID Connect logout"
//         ));
        
//         response.put("server_url", authorizationServerUrl);
//         response.put("endpoints", endpoints);
//         response.put("total_endpoints", endpoints.size());
        
//         return ResponseEntity.ok(response);
//     }

//     @Operation(
//         summary = "OAuth2 Server Health Check", 
//         description = "Basic health check endpoint for the OAuth2 authorization server"
//     )
//     @ApiResponses(value = {
//         @ApiResponse(responseCode = "200", description = "Server is healthy")
//     })
//     @GetMapping("/health")
//     public ResponseEntity<Map<String, Object>> healthCheck() {
//         Map<String, Object> health = new HashMap<>();
//         health.put("status", "UP");
//         health.put("server", "OAuth2 Authorization Server");
//         health.put("timestamp", new Date());
//         health.put("version", "1.0.0");
        
//         return ResponseEntity.ok(health);
//     }

//     @Operation(
//         summary = "Test OAuth2 Authorization URL",
//         description = "Generate a test OAuth2 authorization URL with provided parameters"
//     )
//     @ApiResponses(value = {
//         @ApiResponse(responseCode = "200", description = "Generated authorization URL")
//     })
//     @GetMapping("/oauth2/test-auth-url")
//     public ResponseEntity<Map<String, String>> generateTestAuthUrl(
//             @Parameter(description = "OAuth2 client ID", example = "test-client")
//             @RequestParam String clientId,
//             @Parameter(description = "Redirect URI", example = "https://example.com/callback")
//             @RequestParam String redirectUri,
//             @Parameter(description = "OAuth2 scopes", example = "read write")
//             @RequestParam(defaultValue = "read") String scope,
//             @Parameter(description = "State parameter for CSRF protection", example = "random-state-123")
//             @RequestParam(defaultValue = "test-state") String state) {
        
//         String authUrl = String.format(
//             "%s/oauth2/authorize?response_type=code&client_id=%s&redirect_uri=%s&scope=%s&state=%s",
//             authorizationServerUrl, clientId, redirectUri, scope, state
//         );
        
//         Map<String, String> response = new HashMap<>();
//         response.put("authorization_url", authUrl);
//         response.put("instructions", "Visit this URL to start the OAuth2 authorization flow");
        
//         return ResponseEntity.ok(response);
//     }

//     private Map<String, String> createEndpoint(String name, String method, String path, String description, String notes) {
//         Map<String, String> endpoint = new HashMap<>();
//         endpoint.put("name", name);
//         endpoint.put("method", method);
//         endpoint.put("path", path);
//         endpoint.put("full_url", authorizationServerUrl + path);
//         endpoint.put("description", description);
//         if (!notes.isEmpty()) {
//             endpoint.put("notes", notes);
//         }
//         return endpoint;
//     }
// } 