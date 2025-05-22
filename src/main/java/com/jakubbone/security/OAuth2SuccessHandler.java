package com.jakubbone.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakubbone.model.User;
import com.jakubbone.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private ObjectMapper objectMapper;

    public OAuth2SuccessHandler(UserRepository userRepository, JwtTokenProvider jwtTokenProvider, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String githubLogin = oAuth2User.getAttribute("login");

        User user = userRepository.findByUsername(githubLogin)
                .orElseGet(() -> {
                    User u = new User();
                    u.setUsername(githubLogin);
                    u.setRole(User.Role.USER);
                    u.setProvider("GITHUB");
                    return userRepository.save(u);
                });

        String token = jwtTokenProvider.createToken(user.getUsername(), user.getRole().name());


        Map<String, String> responseBody = Collections.singletonMap("token", token);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), responseBody);
    }
}
