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
    /rivate final ImpersonationService impersonationService;

    public ImpersonationController(ImpersonationService impersonationService) {
        this.impersonationService = impersonationService;
    }

    @PostMapping("/login/impersonation")
    public ResponseEntity<?> impersonate(@RequestParam String targetUsername, Authentication authentication){

        String token = impersonationService.impersonateUser(targetUsername);
        Map<String, String> responseBody = Collections.singletonMap("token", token);
        return ResponseEntity.ok(responseBody);

    }

    @PostMapping("/logout/impersonation")
    public ResponseEntity<?> exitImpersonate(Authentication authentication){
        String token = impersonationService.exitImpersonateUser(authentication.getName());

        Map<String, String> responseBody = Collections.singletonMap("token", token);
        return ResponseEntity.ok(responseBody);
    }
}
