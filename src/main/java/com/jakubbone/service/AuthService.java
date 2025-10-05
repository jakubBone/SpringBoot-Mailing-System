package com.jakubbone.service;

import com.jakubbone.dto.TokenResponse;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public String registerUser(String username, String password,
                               String email, String firstName, String lastName){
        return null;
    }

    public TokenResponse loginUser(String username, String password){
        return new TokenResponse(null, null, null);
    }
}
