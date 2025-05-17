package com.jakubbone.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

/**
 * Filter that validates JWT tokens and enforces ADMIN role.
 * Skipped for public endpoints; throws 403 if role is not ADMIN.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Log4j2
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                // Token validation
                Claims claims = Jwts.parser()
                        .setSigningKey(jwtTokenProvider.getSecretKey())
                        .parseClaimsJws(token)
                        .getBody();

                String username = claims.getSubject();
                String role = claims.get("role", String.class);

                System.out.println("!!!!!!!!!!!!!!!!!!!!" + role);
                addAuthorizationToContext(username, role);

            } catch (JwtException | IllegalArgumentException ex) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired token");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    public void addAuthorizationToContext(String username, String role) {
        // Authentication building with prefix ROLE_
        var authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + role)
        );
        var auth = new UsernamePasswordAuthenticationToken(
                username, null, authorities
        );

        // Add auth to Security Context to authorize request
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}