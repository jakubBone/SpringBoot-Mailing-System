package com.jakubbone.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "User login details")
public class LoginRequest {
    @Schema(description = "Username", example = "johndoe")
    @NotBlank(message = "Username cannot be blank")
    private String username;

    @Schema(description = "Password", example = "Password123!")
    @NotBlank(message = "Password cannot be blank")
    private String password;
}
