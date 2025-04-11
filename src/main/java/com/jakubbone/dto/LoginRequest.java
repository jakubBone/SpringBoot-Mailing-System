package com.jakubbone.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor // @NoArgsConstructor - Jackson requires no args constructor to create JSON request
public class LoginRequest {
    private String username;
    private String password;
}
