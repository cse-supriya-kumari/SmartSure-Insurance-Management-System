package com.smartsure.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartsure.auth.dto.*;
import com.smartsure.auth.security.JwtAuthenticationFilter;
import com.smartsure.auth.security.JwtUtil;
import com.smartsure.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.smartsure.auth.repository.UserRepository;
import com.smartsure.auth.security.UserDetailsServiceImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void register_success() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .name("John Doe")
                .email("john@example.com")
                .password("password123")
                .build();

        AuthResponse response = AuthResponse.builder()
                .token(null)
                .role("CUSTOMER")
                .name("John Doe")
                .build();

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    void login_success() throws Exception {
        LoginRequest request = new LoginRequest("john@example.com", "password123");

        AuthResponse response = AuthResponse.builder()
                .token("sample.jwt.token")
                .role("CUSTOMER")
                .name("John Doe")
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("sample.jwt.token"));
    }

    @Test
    void getUsers_adminSuccess() throws Exception {
        when(authService.getAllUsers()).thenReturn(java.util.List.of(
                UserSummaryDTO.builder()
                        .id(1L)
                        .name("John Doe")
                        .email("john@example.com")
                        .role("CUSTOMER")
                        .status("ACTIVE")
                        .build()
        ));

        mockMvc.perform(get("/api/auth/users")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }
}