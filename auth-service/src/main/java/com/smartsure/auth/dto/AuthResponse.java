package com.smartsure.auth.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    @io.swagger.v3.oas.annotations.media.Schema(description = "JWT authentication token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;
    @io.swagger.v3.oas.annotations.media.Schema(description = "User role", example = "CUSTOMER")
    private String role;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Internal user ID", example = "2")
    private Long userId;
    @io.swagger.v3.oas.annotations.media.Schema(description = "User's full name", example = "Supriya Kumari")
    private String name;
}