package com.jakubbone.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "A new user registration data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @Schema(
            description = "Username",
            example = "johndoe",
            minLength = 3,
            maxLength = 10
    )
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 10, message = "Username must be between 3 and 10 characters long")
    @Pattern(regexp = "^[A-Za-z]{3,10}$",
            message = "Username must contain only letters (A–Z), length 3–10")
    private String username;

    @Schema(
            description = "Password",
            minLength = 8,
            example = "Password123!"
    )
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;


    @Schema(
            description = "Email address",
            example = "username@spring.com"
    )
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @Schema(
            description = "Users first name",
            example = "John"
    )
    @NotBlank(message = "Users first name")
    private String firstName;

    @Schema(
            description = "Users last name",
            example = "Doe"
    )
    @NotBlank(message = "Last name cannot be blank")
    private String lastName;
}
