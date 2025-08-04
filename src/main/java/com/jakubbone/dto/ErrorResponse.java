package com.jakubbone.dto;

public record ErrorResponse(String timestamp, int errorCode, String error, String message) {
}
