package com.smartsure.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @io.swagger.v3.oas.annotations.media.Schema(description = "User's registered email address", example = "admin@smartsure.com")
    @NotBlank(message = "Email is required")
    @jakarta.validation.constraints.Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Email should be valid (regex)")
    private String email;

    @io.swagger.v3.oas.annotations.media.Schema(description = "User's password", example = "Admin@123")
    @NotBlank(message = "Password is required")
    private String password;
}