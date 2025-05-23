package com.jakubbone.controller;

import com.jakubbone.service.ImpersonationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("api/v1")
public class ImpersonationController {
    private final ImpersonationService impersonationService;

    public ImpersonationController(ImpersonationService impersonationService) {
        this.impersonationService = impersonationService;
    }

    @PostMapping("/impersonation")
    public ResponseEntity<?> impersonate(@RequestParam String targetUsername, Authentication authentication){
        String token = impersonationService.impersonateUser(targetUsername);
        Map<String, String> responseBody = Collections.singletonMap("token", token);
        return ResponseEntity.ok(responseBody);
    }
}
