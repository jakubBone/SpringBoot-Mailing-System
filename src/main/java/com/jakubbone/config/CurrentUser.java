package com.jakubbone.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {
        private final String username;

        public CurrentUser(Authentication authentication) {
            if (authentication instanceof JwtAuthenticationToken jwt) {
                this.username = jwt.getToken().getClaimAsString("preferred_username");
            } else {
                this.username = null;
            }
        }
        public String getUsername() { return username; }
}
