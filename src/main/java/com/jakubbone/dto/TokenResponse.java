package com.jakubbone.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing access token")
public record TokenResponse(
        @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1...")
        String accessToken,

        @Schema(description = "JWT token expire time", example = "360")
        Integer expireTime,

        @Schema(description = "JWT token Type", example = "Bearer")
        String tokenType) {
}
