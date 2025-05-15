package com.jakubbone.config;

import com.jakubbone.utils.JwtTokenFilter;
import com.jakubbone.utils.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;

/**
 * Security configuration enabling JWT filter and method security.
 * All requests are permitted here; method-level annotations enforce roles.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, UserDetailsService userDetailsService) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/login/v1/impersonate").hasRole("ADMIN")
                        .requestMatchers("/api/logout/v1/impersonate").authenticated()
                        .requestMatchers(("/api/v1/messages")).hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/v1/login", "/api/v1/register", "/api/v1/info", "/api/v1/uptime").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtTokenFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(switchUserFilter(userDetailsService), FilterSecurityInterceptor.class);
        return http.build();
    }

    @Bean
    public SwitchUserFilter switchUserFilter(UserDetailsService userDetailsService) {
        SwitchUserFilter filter = new SwitchUserFilter();
        filter.setUserDetailsService(userDetailsService);
        filter.setSwitchUserUrl("/api/login/v1/impersonate");
        filter.setExitUserUrl("/api/logout/v1/impersonate");
        filter.setTargetUrl("/");
        filter.setExitUserUrl("/");
        return filter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
