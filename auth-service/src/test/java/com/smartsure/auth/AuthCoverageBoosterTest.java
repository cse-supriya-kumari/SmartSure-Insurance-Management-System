package com.smartsure.auth;

import com.smartsure.auth.dto.*;
import com.smartsure.auth.entity.User;
import com.smartsure.auth.entity.Role;
import com.smartsure.auth.exception.*;
import com.smartsure.auth.exception.GlobalExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthCoverageBoosterTest {

    @Test
    void testDtosAndEntities() {
        // Test User Entity
        User user = User.builder()
                .id(1L)
                .name("Test")
                .email("test@test.com")
                .password("pwd")
                .phone("123")
                .address("addr")
                .role(Role.ADMIN)
                .status("ACTIVE")
                .build();
        
        assertEquals(1L, user.getId());
        assertEquals("Test", user.getName());
        assertEquals("test@test.com", user.getEmail());
        assertEquals("pwd", user.getPassword());
        assertEquals("123", user.getPhone());
        assertEquals("addr", user.getAddress());
        assertEquals(Role.ADMIN, user.getRole());
        assertEquals("ACTIVE", user.getStatus());

        User emptyUser = new User();
        emptyUser.setId(2L);
        assertEquals(2L, emptyUser.getId());

        // Test DTOs
        AuthResponse authResponse = new AuthResponse("token", "role", 1L, "name");
        assertEquals("token", authResponse.getToken());
        
        LoginRequest loginRequest = new LoginRequest("e", "p");
        assertEquals("e", loginRequest.getEmail());
        assertEquals("p", loginRequest.getPassword());

        RegisterRequest registerRequest = RegisterRequest.builder().name("n").build();
        assertEquals("n", registerRequest.getName());

        UserSummaryDTO summary = UserSummaryDTO.builder().id(1L).build();
        assertEquals(1L, summary.getId());

        UserStatusUpdateRequest update = new UserStatusUpdateRequest("S");
        assertEquals("S", update.getStatus());

        ApiErrorResponse error = ApiErrorResponse.builder().message("m").build();
        assertEquals("m", error.getMessage());
    }

    @Test
    void testGlobalExceptionHandler() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/test");

        // Test Exception types
        ResponseEntity<ApiErrorResponse> response;

        response = handler.handleNotFound(new ResourceNotFoundException("Not found"), request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        response = handler.handleDuplicate(new DuplicateResourceException("Duplicate"), request);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

        response = handler.handleInvalidOperation(new InvalidOperationException("Invalid"), request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        response = handler.handleUnauthorized(new BadCredentialsException("Bad"), request);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        response = handler.handleGeneric(new Exception("Error"), request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        
        // Test Validation Exception
        MethodArgumentNotValidException valEx = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(valEx.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(new FieldError("obj", "field", "default message")));
        
        response = handler.handleValidation(valEx, request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
