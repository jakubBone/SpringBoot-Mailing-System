package com.jakubbone.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Specific error response structure")
public record ErrorResponse(
        @Schema(description = "Error signature", example = "2025-01-15T10:30:45Z")
        String timestamp,

        @Schema(description = "HTTP error code", example = "400")
        int errorCode,

        @Schema(description = "Short error description", example = "Bad Request")
        String error,

        @Schema(description = "Extended error description")
        String message) {
}
