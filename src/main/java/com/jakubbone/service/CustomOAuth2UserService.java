package com.jakubbone.service;

import com.jakubbone.model.User;
import com.jakubbone.repository.UserRepository;
import com.jakubbone.utils.JwtTokenProvider;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    private final JwtTokenProvider jwtTokenProvider;

    public CustomOAuth2UserService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String githubLogin = oAuth2User.getAttribute("login");

        User user = userRepository.findByUsername(githubLogin)
                .orElseGet(() -> {
                    User u = new User();
                    u.setUsername(githubLogin);
                    u.setRole(User.Role.USER);
                    u.setProvider("GITHUB");
                    return userRepository.save(u);
                });

        // Generate token
        String jwt = jwtTokenProvider.createToken(user.getUsername(), user.getRole().name());

        // JWT logging
        System.out.println("\n===== JWT for user " + githubLogin + " =====\n" + jwt + "\n==========================\n");

        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("jwt", jwt);

        return new DefaultOAuth2User(oAuth2User.getAuthorities(), attributes, "login");
    }
}
