package com.jakubbone.dto;

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
public class SendMessageRequest {
    @NotBlank(message = "Recipient username cannot be blank")
    @Size(min = 3, max = 10, message = "Username must be between 3 and 10 characters long")
    @Pattern(regexp = "^[A-Za-z]{3,10}$",
            message = "Username must contain only letters (A–Z), length 3–10")
    private String to;

    @NotBlank(message = "Message text cannot be blank")
    @Size(min = 1, max = 256, message = "Message text must be between 1 and 256 characters long")
    private String text;
}
