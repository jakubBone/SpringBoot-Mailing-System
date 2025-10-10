package com.jakubbone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for RestTemplate bean used for HTTP client operations.
 * RestTemplate is used primarily for communicating with Keycloak token endpoint.
 */
@Configuration
public class RestTamplateConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
