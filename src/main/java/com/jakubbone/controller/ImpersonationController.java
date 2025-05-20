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
@RequestMapping("api/admin/v1")
public class ImpersonationController {
    private final ImpersonationService impersonationService;

    public ImpersonationController(ImpersonationService impersonationService) {
        this.impersonationService = impersonationService;
    }

    @PostMapping("/login/impersonation")
    public ResponseEntity<?> impersonate(@RequestParam String targetUsername, Authentication authentication){
        boolean isAdmin = authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN"));

        if(!isAdmin){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin role required");
        }

        String token = impersonationService.impersonateUser(targetUsername);
        Map<String, String> responseBody = Collections.singletonMap("token", token);
        return ResponseEntity.ok(responseBody);

    }

    @PostMapping("/logout/impersonation")
    public ResponseEntity<?> exitImpersonate(Authentication authentication){
        boolean isPreviousAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_PREVIOUS_ADMINISTRATOR"));

        if(!isPreviousAdmin){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not in impersonated mode");
        }

        String token = impersonationService.exitImpersonateUser(authentication.getName());

        Map<String, String> responseBody = Collections.singletonMap("token", token);
        return ResponseEntity.ok(responseBody);
    }
}
