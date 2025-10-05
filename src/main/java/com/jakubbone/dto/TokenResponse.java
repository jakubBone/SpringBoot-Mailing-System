package com.jakubbone.dto;


public record TokenResponse(String accessToken, Integer expireTime, String tokenType) {
}
