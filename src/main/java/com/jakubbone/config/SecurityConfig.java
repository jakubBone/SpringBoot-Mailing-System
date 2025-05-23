package com.jakubbone.config;

import com.jakubbone.utils.KeycloakRoleConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration enabling JWT filter and method security.
 * All requests are permitted here; method-level annotations enforce roles.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Stateless REST API using JWT tokens only (no cookies/session)
        // CSRF protection is not needed here
        // Disable it to avoid 403 code on POST/PUT/DELETE
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(("/api/admin/v1/login/impersonation")).hasRole("ADMIN")
                        .requestMatchers(("/api/admin/v1/logout/impersonation")).hasRole("PREVIOUS_ADMINISTRATOR")
                        .requestMatchers(("/api/v1/messages")).hasAnyRole("USER", "ADMIN", "PREVIOUS_ADMINISTRATOR")
                        .requestMatchers( "/api/v1/info", "/api/v1/uptime").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );
                /*.oauth2ResourceServer(oauth2 -> oauth2
                        .jwt() // automatic JWT token verification with Keycloak
                );*/
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        return converter;
    }

}
