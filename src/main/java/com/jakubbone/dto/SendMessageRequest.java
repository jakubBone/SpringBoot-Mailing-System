package com.jakubbone.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor // @NoArgsConstructor - Jackson requires no args constructor to create JSON request
public class SendMessageRequest {
    @NotBlank(message = "Recipient username cannot be blank")
    private String to;

    @NotBlank(message = "Message text cannot be blank")
    private String text;

}
