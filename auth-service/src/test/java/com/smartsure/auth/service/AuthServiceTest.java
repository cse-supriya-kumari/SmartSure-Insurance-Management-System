package com.smartsure.auth.service;

import com.smartsure.auth.dto.*;
import com.smartsure.auth.entity.Role;
import com.smartsure.auth.entity.User;
import com.smartsure.auth.exception.DuplicateResourceException;
import com.smartsure.auth.repository.UserRepository;
import com.smartsure.auth.security.JwtUtil;
import com.smartsure.auth.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .password("encoded_password")
                .phone("9999999999")
                .address("Test Address")
                .role(Role.CUSTOMER)
                .status("ACTIVE")
                .build();
    }

    @Test
    void testRegister_success_customer() {
        RegisterRequest request = RegisterRequest.builder()
                .name("John Doe")
                .email("john@example.com")
                .password("Password123!") // Valid password
                .phone("9999999999")
                .address("Test Address")
                .role("CUSTOMER")
                .build();

        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        // when(jwtUtil.generateToken(...)) is no longer called during registration

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertNull(response.getToken());
        assertEquals("CUSTOMER", response.getRole());
    }

    @Test
    void testRegister_invalidPassword_tooShort() {
        RegisterRequest request = RegisterRequest.builder()
                .name("John")
                .email("john@example.com")
                .password("Pass1")
                .build();

        assertThrows(com.smartsure.auth.exception.InvalidOperationException.class, 
                     () -> authService.register(request));
    }

    @Test
    void testRegister_invalidPassword_noUppercase() {
        RegisterRequest request = RegisterRequest.builder()
                .name("John")
                .email("john@example.com")
                .password("password123")
                .build();

        assertThrows(com.smartsure.auth.exception.InvalidOperationException.class, 
                     () -> authService.register(request));
    }

    @Test
    void testValidateTokenAndGetUser_success() {
        when(jwtUtil.extractUsername("ValidToken")).thenReturn("john@example.com");
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(sampleUser));

        UserSummaryDTO response = authService.validateTokenAndGetUser("ValidToken");

        assertNotNull(response);
        assertEquals("John Doe", response.getName());
        assertEquals("CUSTOMER", response.getRole());
    }

    @Test
    void testRegister_invalidPassword_noNumber() {
        RegisterRequest request = RegisterRequest.builder()
                .name("John")
                .email("john@example.com")
                .password("Password")
                .build();

        assertThrows(com.smartsure.auth.exception.InvalidOperationException.class, 
                     () -> authService.register(request));
    }

    @Test
    void testRegister_emailAlreadyExists() {
        RegisterRequest request = RegisterRequest.builder()
                .name("John Doe")
                .email("john@example.com")
                .password("Password123!")
                .build();

        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));
    }

    @Test
    void testLogin_success() {
        LoginRequest request = new LoginRequest("john@example.com", "Password123!");
        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userDetails = new UserDetailsImpl(sampleUser);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(sampleUser));
        when(jwtUtil.generateToken(userDetails, "CUSTOMER")).thenReturn("sample.jwt.token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("sample.jwt.token", response.getToken());
    }

    @Test
    void testLogin_badCredentials() {
        LoginRequest request = new LoginRequest("john@example.com", "wrong");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(com.smartsure.auth.exception.UnauthorizedException.class, () -> authService.login(request));
    }

    @Test
    void testLogin_inactiveAccount() {
        LoginRequest request = new LoginRequest("john@example.com", "Password123!");
        sampleUser.setStatus("INACTIVE");
        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userDetails = new UserDetailsImpl(sampleUser);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(sampleUser));

        assertThrows(com.smartsure.auth.exception.UnauthorizedException.class, () -> authService.login(request));
    }

    @Test
    void testUpdateUserStatus() {
        UserStatusUpdateRequest request = new UserStatusUpdateRequest("INACTIVE");
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        UserSummaryDTO response = authService.updateUserStatus(1L, request);

        assertEquals("INACTIVE", response.getStatus());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testGetUserById_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        UserSummaryDTO user = authService.getUserById(1L);

        assertEquals("John Doe", user.getName());
    }
}