package com.jakubbone.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
public class JwtTokenFilter extends OncePerRequestFilter {
    private JwtTokenProvider jwtTokenProvider;

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

                if (!"ADMIN".equals(role)) {
                    response.sendError(HttpStatus.UNAUTHORIZED.value(), "Admin role required");
                    return;
                }

                // Authentication building with prefix ROLE_
                var authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_" + role)
                );
                var auth = new UsernamePasswordAuthenticationToken(
                        username, null, authorities
                );

                // 3) Add auth to Security Context to authorize request
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (JwtException | IllegalArgumentException ex) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired token");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}