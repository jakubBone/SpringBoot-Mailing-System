package com.jakubbone.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;

/**
 * Configuration for OpenAPI 3.0 documentation.
 * Provides API metadata and JWT Bearer token authentication scheme for Swagger UI.
 *
 * NOTE: In Docker environment, users need to obtain JWT token from Keycloak
 * at http://localhost:8180/realms/mailingsystem before using the API.
 */
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenApi(){
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("SpringBootMailingSystem API")
                        .version("1.0.0")
                        .description("""
                                REST API for a simple mailing system with user authentication via Keycloak OAuth2/JWT.
                                
                                **Authentication Instructions:**
                                1. Obtain JWT token from Keycloak (see below)
                                2. Click the 'Authorize' button (green lock icon)
                                3. Paste your token in the 'Value' field
                                4. Click 'Authorize' and 'Close'
                                
                                **Getting JWT Token (Docker environment):**
                                ```bash
                                curl -X POST http://localhost:8180/realms/mailingsystem/protocol/openid-connect/token \\
                                  -H "Content-Type: application/x-www-form-urlencoded" \\
                                  -d "client_id=mailing-app" \\
                                  -d "client_secret=YOUR_CLIENT_SECRET" \\
                                  -d "grant_type=password" \\
                                  -d "username=YOUR_USERNAME" \\
                                  -d "password=YOUR_PASSWORD"
                                ```
                                
                                **Available test users:**
                                - Admin: testadmin / adminPassword
                                - User: testuser / userPassword
                                """);
    }
}
