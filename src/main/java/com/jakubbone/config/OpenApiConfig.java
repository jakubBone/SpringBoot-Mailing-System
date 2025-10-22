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
                        .description("API documentation for mailing system"))


                .addSecurityItem(new SecurityRequirement().addList("keycloak_oauth"))

                .components(new Components()
                        .addSecuritySchemes("keycloak_oauth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.OAUTH2)
                                        .description("Keycloak OAuth2 - Resource Owner Password Flow")
                                        .flows(new OAuthFlows()
                                                .password(new OAuthFlow()
                                                        .tokenUrl(tokenUrl)
                                                        .scopes(new Scopes()
                                                                .addString("openid", "OpenID Connect scope")
                                                                .addString("profile", "Profile scope")
                                                                .addString("email", "Email scope"))))));
    }
}
