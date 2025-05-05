package com.jakubbone.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendMessageRequest {
    @NotBlank(message = "Recipient username cannot be blank")
    private String to;

    @NotBlank(message = "Message text cannot be blank")
    private String text;

}
