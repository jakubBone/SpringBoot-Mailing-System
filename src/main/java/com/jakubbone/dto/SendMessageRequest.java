package com.jakubbone.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Message specific message to send data")
public class SendMessageRequest {
    @Schema(
            description = "Recipient username",
            example = "johndoe",
            minLength = 3,
            maxLength = 10
    )
    @NotBlank(message = "Recipient username cannot be blank")
    @Size(min = 3, max = 10, message = "Username must be between 3 and 10 characters long")
    @Pattern(regexp = "^[A-Za-z]{3,10}$",
            message = "Username must contain only letters (A–Z), length 3–10")
    private String to;

    @Schema(
            description = "Message content",
            example = "Hello user!",
            minLength = 1,
            maxLength = 256
    )
    @NotBlank(message = "Message text cannot be blank")
    @Size(min = 1, max = 256, message = "Message text must be between 1 and 256 characters long")
    private String text;
}
