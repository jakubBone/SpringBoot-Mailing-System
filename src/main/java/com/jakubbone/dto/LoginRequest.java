package com.jakubbone.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor // @NoArgsConstructor - Jackson requires no args constructor to create JSON request
public class LoginRequest {
    private String username;
    private String password;
}
