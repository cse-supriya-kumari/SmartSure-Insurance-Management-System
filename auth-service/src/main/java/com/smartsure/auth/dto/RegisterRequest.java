package com.smartsure.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @io.swagger.v3.oas.annotations.media.Schema(description = "Full name of the user", example = "Supriya Kumari")
    @NotBlank(message = "Name is required")
    private String name;

    @io.swagger.v3.oas.annotations.media.Schema(description = "User's email address", example = "supriya@example.com")
    @NotBlank(message = "Email is required")
    @jakarta.validation.constraints.Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Email should be valid (regex)")
    private String email;

    @io.swagger.v3.oas.annotations.media.Schema(description = "Secure password for the account", example = "Password@123")
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @io.swagger.v3.oas.annotations.media.Schema(description = "Contact phone number", example = "9876543210")
    @jakarta.validation.constraints.Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone must be a valid 10-digit Indian number")
    private String phone;

    @io.swagger.v3.oas.annotations.media.Schema(description = "Residential address", example = "Bangalore, India")
    private String address;

    @io.swagger.v3.oas.annotations.media.Schema(description = "User role (CUSTOMER, ADMIN)", example = "CUSTOMER")
    private String role;
}