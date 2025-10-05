package com.jakubbone.dto;

public record TokenResponse(String accessToken, int expireTime, String tokenType) {
}
