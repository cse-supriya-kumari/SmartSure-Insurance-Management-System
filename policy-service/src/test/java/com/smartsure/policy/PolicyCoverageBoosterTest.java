package com.smartsure.policy;

import com.smartsure.policy.dto.*;
import com.smartsure.policy.entity.*;
import com.smartsure.policy.exception.GlobalExceptionHandler;
import com.smartsure.policy.exception.ResourceNotFoundException;
import com.smartsure.policy.exception.InvalidOperationException;
import com.smartsure.policy.config.IdempotencyInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PolicyCoverageBoosterTest {

    @Test
    void testEntitiesAndDtos() {
        PolicyType type = PolicyType.builder()
                .id(1L).name("T").description("D").basePremium(BigDecimal.ONE)
                .coverageAmount(BigDecimal.TEN).durationMonths(12).build();
        assertEquals(1L, type.getId());
        assertEquals("T", type.getName());

        Policy policy = Policy.builder().id(1L).userId(1L).policyType(type).build();
        assertEquals(1L, policy.getId());

        Premium premium = Premium.builder().id(1L).policy(policy).amount(BigDecimal.ONE).status(PremiumStatus.PENDING).build();
        assertEquals(PremiumStatus.PENDING, premium.getStatus());

        // DTOs
        PolicyDTO dto = PolicyDTO.builder().id(1L).build();
        assertEquals(1L, dto.getId());

        PolicyTypeDTO typeDto = PolicyTypeDTO.builder().id(1L).build();
        assertEquals(1L, typeDto.getId());

        PolicyPurchaseRequest purchaseRequest = new PolicyPurchaseRequest(1L, 1L);
        assertEquals(1L, purchaseRequest.getUserId());
    }

    @Test
    void testGlobalExceptionHandler() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/test");

        ResponseEntity<?> response;
        response = handler.handleNotFound(new ResourceNotFoundException("Not found"), request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        response = handler.handleInvalidOperation(new InvalidOperationException("Invalid"), request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testIdempotencyInterceptor() throws Exception {
        IdempotencyInterceptor interceptor = new IdempotencyInterceptor();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        java.io.PrintWriter writer = mock(java.io.PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);
        
        when(request.getMethod()).thenReturn("POST");
        when(request.getHeader("Idempotency-Key")).thenReturn("key1");
        
        assertTrue(interceptor.preHandle(request, response, new Object()));
        // Second call with same key
        assertFalse(interceptor.preHandle(request, response, new Object()));
    }
}
