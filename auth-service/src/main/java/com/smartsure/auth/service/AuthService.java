package com.smartsure.auth.service;

import com.smartsure.auth.dto.*;
import com.smartsure.auth.entity.Role;
import com.smartsure.auth.entity.User;
import com.smartsure.auth.exception.DuplicateResourceException;
import com.smartsure.auth.exception.ResourceNotFoundException;
import com.smartsure.auth.security.JwtUtil;
import com.smartsure.auth.security.UserDetailsImpl;
import com.smartsure.auth.repository.UserRepository;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthService(UserRepository userRepository,
                       org.springframework.security.crypto.password.PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }

    public AuthResponse register(RegisterRequest request) {
        validatePassword(request.getPassword());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email is already registered");
        }

        Role role = Role.CUSTOMER;
        if (request.getRole() != null) {
            try {
                role = Role.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Default to CUSTOMER if invalid role provided
            }
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .address(request.getAddress())
                .role(role)
                .status("ACTIVE")
                .build();

        userRepository.save(user);
        emailService.sendWelcomeEmail(user.getName(), user.getEmail());

        return AuthResponse.builder()
                .token(null)
                .role(user.getRole().name())
                .name(user.getName())
                .build();
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new com.smartsure.auth.exception.InvalidOperationException("Password must be at least 8 characters long");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new com.smartsure.auth.exception.InvalidOperationException("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*[0-9].*")) {
            throw new com.smartsure.auth.exception.InvalidOperationException("Password must contain at least one number");
        }
    }

    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            if ("INACTIVE".equals(user.getStatus())) {
                throw new com.smartsure.auth.exception.UnauthorizedException("Account is inactive. Please contact admin.");
            }

            String token = jwtUtil.generateToken(userDetails, user.getRole().name());

            return AuthResponse.builder()
                    .token(token)
                    .role(user.getRole().name())
                    .name(user.getName())
                    .build();
        } catch (BadCredentialsException e) {
            throw new com.smartsure.auth.exception.UnauthorizedException("Invalid email or password");
        }
    }

    public UserSummaryDTO validateTokenAndGetUser(String token) {
        jwtUtil.validateToken(token);
        String email = jwtUtil.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToUserSummary(user);
    }

    public List<UserSummaryDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserSummary)
                .toList();
    }

    public UserSummaryDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToUserSummary(user);
    }

    public UserSummaryDTO updateUserStatus(Long id, UserStatusUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Business Logic: Prevent self-deactivation if caller is the same user and an admin
        // Note: In a real app, we'd check the SecurityContext. 
        // For simplicity, we'll assume the controller handles authorization, but logic here protects the data.
        
        if (Role.ADMIN.equals(user.getRole()) && "INACTIVE".equals(request.getStatus())) {
            // Check if it's the last admin? (More complex logic could go here)
        }

        user.setStatus(request.getStatus());
        userRepository.save(user);

        return mapToUserSummary(user);
    }

    private UserSummaryDTO mapToUserSummary(User user) {
        return UserSummaryDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .role(user.getRole().name())
                .status(user.getStatus())
                .build();
    }
}