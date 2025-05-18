package com.jakubbone.service;

import com.jakubbone.model.User;
import com.jakubbone.repository.UserRepository;
import com.jakubbone.utils.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ImpersonationService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public ImpersonationService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String impersonateUser(String targetUsername){
        User targetUser = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + targetUsername));

        return jwtTokenProvider.createImpersonationToken(targetUser.getUsername());
    }

    public String exitImpersonateUser(String adminUsername){
        User adminUser = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));

        String token = jwtTokenProvider.createToken(
                adminUser.getUsername(),
                String.valueOf(adminUser.getRole())
        );
        return token;
    }
}
