package com.jakubbone.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        String tokenUrl = "http://localhost:8180/realms/mailingsystem/protocol/openid-connect/token";

        return new OpenAPI()
                .info(new Info()
                        .title("Spring Boot Mailing System API")
                        .version("1.0.0")
                        .description("""
                            RESTful API for a messaging system with OAuth2 authentication via Keycloak.
                            
                            ## Quick Start - Testing the API
                            
                            ## Testing Flow Example
                            
                            **Scenario 1: With new user registration**
                            1. Register new user "johndoe": POST /api/v1/auth/register
                            2. Authorize as "johndoe"
                            3. Send message to "admin"
                            4. Logout
                            5. Authorize as "admin"
                            6. Read messages from "johndoe"
                            7. Reply to "johndoe"
                            
                            **Scenario 2: Using pre-configured users**
                            1. Authorize as "admin" with password "java10"
                            2. Send message "hello admin!" to "testuser": POST /api/v1/messages
                            3. Logout 
                            4. Authorize as "testuser" with password "java10"
                            5. Read messages: GET /api/v1/messages
                            6. Search messages: GET /api/v1/messages/search?phrase=hello
                            
                            Any registered user can communicate with any other user in the system

                            **Important:** You must register a user BEFORE you can authorize as that user.

                            ## Test Users
                            
                            **Pre-configured accounts:**
                            - **testuser** / userPassword = "java10"
                            - **admin**/ userPassword = "java10"
                            
                            ## Features
                            
                            - User registration and authentication
                            - Send and receive messages between users
                            - Full-text search in message content
                            - Automatic message read status tracking
                            - Role-based access control (USER, ADMIN)
                            - Input validation and XSS protection
                            
                            ## Validation Rules
                            
                            - **Username:** 3-10 letters only
                            - **Password:** Minimum 8 characters
                            - **Message:** 1-256 characters, HTML sanitized
                            - **Mailbox limit:** 5 unread messages per user

                            ## Token Expiration
                            
                            JWT tokens expire after 5 minutes (300 seconds). 
                            If you receive 401 errors, re-authenticate to obtain a new token.
                            
                            ## Error Responses
                            
                            - **400** Bad Request - Validation failed
                            - **401** Unauthorized - Authentication required
                            - **403** Forbidden - Insufficient permissions
                            - **404** Not Found - Resource does not exist
                            - **409** Conflict - Business rule violation (e.g., mailbox full)
                            - **500** Internal Server Error - Unexpected error
                            """))
                .addSecurityItem(new SecurityRequirement().addList("keycloak_oauth"))
                .components(new Components()
                        .addSecuritySchemes("keycloak_oauth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.OAUTH2)
                                        .description("""
                                            OAuth 2.0 Password Grant flow with Keycloak.
                                            
                                            Tokens are valid for 5 minutes and must be included 
                                            in the Authorization header as: Bearer <token>
                                            """)
                                        .flows(new OAuthFlows()
                                                .password(new OAuthFlow()
                                                        .tokenUrl(tokenUrl)
                                                        .scopes(new Scopes()
                                                                .addString("openid", "OpenID Connect authentication")
                                                                .addString("profile", "User profile information")
                                                                .addString("email", "User email address"))))));
    }
}
