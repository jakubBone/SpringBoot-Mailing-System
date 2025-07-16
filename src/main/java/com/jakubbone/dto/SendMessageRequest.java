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
    @Pattern(regexp = "^[A-Za-z]{3,10}$", message = "s")
    private String to;

    @NotBlank(message = "Message text cannot be blank")
    @Size(min = 1, max = 256, message = "Message text must be between 1 and 256 characters")
    private String text;
}
