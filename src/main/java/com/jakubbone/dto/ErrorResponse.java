package com.jakubbone.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private String timestamp;
    private int errorCode;
    private String error;
    private String message;
}
