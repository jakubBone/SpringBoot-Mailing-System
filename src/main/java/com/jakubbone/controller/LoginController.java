package com.jakubbone.controller;

import com.jakubbone.dto.LoginRequest;
import com.jakubbone.model.User;
import com.jakubbone.repository.UserRepository;
import com.jakubbone.utils.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class LoginController {
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginController(PasswordEncoder encoder, UserRepository userRepository, JwtTokenProvider tokenProvider) {
        this.encoder = encoder;
        this.userRepository = userRepository;
        this.jwtTokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        String username = req.getUsername();
        String password = req.getPassword();

        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

        User user = userOpt.get();

        if (!encoder.matches(password, user.getPasswordHash())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

        String token = jwtTokenProvider.createToken(user.getUsername(), String.valueOf(user.getRole()));
        Map<String, String> responseBody = Collections.singletonMap("token", token);
        return ResponseEntity.ok(responseBody);
    }
}
