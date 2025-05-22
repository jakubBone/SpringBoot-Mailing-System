package com.jakubbone.config;

import com.jakubbone.security.OAuth2SuccessHandler;
import com.jakubbone.security.JwtTokenFilter;
import com.jakubbone.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider, OAuth2SuccessHandler oAuth2SuccessHandler) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
    }

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
                .oauth2Login(oauth2 -> oauth2
                                .successHandler(oAuth2SuccessHandler)
                )
                .addFilterBefore(new JwtTokenFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
