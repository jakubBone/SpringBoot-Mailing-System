package com.jakubbone.controller;

import com.jakubbone.dto.LoginRequest;
import com.jakubbone.dto.SendMessageRequest;
import com.jakubbone.repository.UserRepository;
import com.jakubbone.utils.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
public class ImpersonationController {
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public ImpersonationController(PasswordEncoder encoder, UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.encoder = encoder;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login/impersonate")
    public ResponseEntity<?> impersonate(@RequestBody LoginRequest req, Authentication authentication){

    }

}
