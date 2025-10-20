package com.jakubbone.controller;

import com.jakubbone.dto.LoginRequest;
import com.jakubbone.dto.RegisterRequest;
import com.jakubbone.dto.TokenResponse;
import com.jakubbone.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication controller for user registration and login.
 * Handles user management in Keycloak and JWT token generation.
 */
@Tag(name = "Authentication", description = "User registration and login endpoints")
@RestController
@RequestMapping("api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account in the system",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User successfully registered"),
                    @ApiResponse(responseCode = "409", description = "User already exists")
            }
    )
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest req){
        authService.registerUser(req.getUsername(),
                req.getPassword(),
                req.getEmail(),
                req.getFirstName(),
                req.getLastName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User registered successfully. You can now login.");
    }

    @Operation(
            summary = "User login",
            description = "Authenticates user and generates JWT token",
            responses = {
                    @ApiResponse(responseCode = "201", description = "JWT token response Json"),
                    @ApiResponse(responseCode = "401", description = "Invalid username or password")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest req){
        TokenResponse response = authService.loginUser(req.getUsername(), req.getPassword());
        return ResponseEntity.ok(response);
    }
}
