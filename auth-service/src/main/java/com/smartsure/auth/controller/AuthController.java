package com.smartsure.auth.controller;

import com.smartsure.auth.dto.*;
import com.smartsure.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {

    private final AuthService authService;
    private final com.smartsure.auth.repository.UserRepository userRepository;

    public AuthController(AuthService authService,
                          com.smartsure.auth.repository.UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @Operation(summary = "User Registration", description = "Create a new user account")
    @ApiResponse(responseCode = "200", description = "User registered successfully", 
        content = @Content(schema = @Schema(implementation = AuthResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input or user already exists")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(summary = "User Login", description = "Authenticate user and return JWT token")
    @ApiResponse(responseCode = "200", description = "Successfully authenticated", 
        content = @Content(schema = @Schema(implementation = AuthResponse.class)))
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Validate Token", description = "Validate a JWT token and return the associated user details")
    @ApiResponse(responseCode = "200", description = "Token is valid",
        content = @Content(schema = @Schema(implementation = UserSummaryDTO.class)))
    @ApiResponse(responseCode = "401", description = "Invalid or expired token")
    @GetMapping("/validate")
    public ResponseEntity<UserSummaryDTO> validateToken(@Parameter(hidden = true) @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        return ResponseEntity.ok(authService.validateTokenAndGetUser(token));
    }

    @Operation(summary = "Get Total User Count", description = "Returns the total number of registered users (Admin only)")
    @ApiResponse(responseCode = "200", description = "Count returned successfully")
    @ApiResponse(responseCode = "403", description = "Access denied — ADMIN role required")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/count")
    public ResponseEntity<Long> getTotalUsers() {
        return ResponseEntity.ok(userRepository.count());
    }

    @Operation(summary = "Get All Users", description = "Retrieve a complete list of all registered users (Admin only)")
    @ApiResponse(responseCode = "200", description = "List of users returned")
    @ApiResponse(responseCode = "403", description = "Access denied — ADMIN role required")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<UserSummaryDTO>> getAllUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
    }

    @Operation(summary = "Get User by ID", description = "Retrieve a single user's details by their ID (Admin only)")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "403", description = "Access denied — ADMIN role required")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/{id}")
    public ResponseEntity<UserSummaryDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(authService.getUserById(id));
    }

    @Operation(summary = "Update User Status", description = "Activate or deactivate a user account by setting their status (Admin only)")
    @ApiResponse(responseCode = "200", description = "User status updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid status value")
    @ApiResponse(responseCode = "404", description = "User not found")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{id}/status")
    public ResponseEntity<UserSummaryDTO> updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody UserStatusUpdateRequest request) {
        return ResponseEntity.ok(authService.updateUserStatus(id, request));
    }
}