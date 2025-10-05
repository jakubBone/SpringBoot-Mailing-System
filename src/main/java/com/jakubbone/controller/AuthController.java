package com.jakubbone.controller;

import com.jakubbone.dto.LoginRequest;
import com.jakubbone.dto.RegisterRequest;
import com.jakubbone.dto.SendMessageRequest;
import com.jakubbone.dto.TokenResponse;
import com.jakubbone.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.EntityResponse;

/**
 * Authentication controller for user registration and login.
 * Handles user management in Keycloak and JWT token generation.
 */
@RestController
@RequestMapping("api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest req){
        authService.registerUser(req.getUsername(), req.getPassword(),
                req.getEmail(), req.getFirstName(), req.getLastName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User registered successfully. You can now login.");
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest req){
        TokenResponse response = authService.loginUser(req.getUsername(), req.getPassword());
        return ResponseEntity.ok(response);
    }

}
