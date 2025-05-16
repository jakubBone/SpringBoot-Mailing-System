package com.jakubbone.service;

import com.jakubbone.model.User;
import com.jakubbone.repository.UserRepository;
import com.jakubbone.utils.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Map;

@Service
public class ImpersonationService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public ImpersonationService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String impersonateUser(String adminUsername, String targetUsername){
        User targetUser = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + targetUsername));

        String token = jwtTokenProvider.createImpersonationToken(
                adminUsername,
                targetUser.getUsername(),
                String.valueOf(targetUser.getRole()
        );
        return token;
    }

    public String exitImpersonateUser(String adminUsername){
        User targetUser = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));

        String token = jwtTokenProvider.createToken(
                targetUser.getUsername(),
                String.valueOf(targetUser.getRole()
        );
        return token;
    }
}
