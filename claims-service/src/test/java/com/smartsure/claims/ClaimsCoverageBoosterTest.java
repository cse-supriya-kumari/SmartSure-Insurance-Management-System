package com.smartsure.claims;

import com.smartsure.claims.dto.*;
import com.smartsure.claims.entity.*;
import com.smartsure.claims.exception.GlobalExceptionHandler;
import com.smartsure.claims.exception.ResourceNotFoundException;
import com.smartsure.claims.exception.InvalidOperationException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClaimsCoverageBoosterTest {

    @Test
    void testEntitiesAndDtos() {
        Claim claim = Claim.builder().id(1L).policyId(1L).userId(1L).description("D").status(ClaimStatus.SUBMITTED).build();
        assertEquals(1L, claim.getId());
        assertEquals(ClaimStatus.SUBMITTED, claim.getStatus());

        ClaimDocument doc = ClaimDocument.builder().id(1L).claim(claim).fileName("f").build();
        assertEquals("f", doc.getFileName());

        // DTOs
        ClaimDTO dto = ClaimDTO.builder().id(1L).build();
        assertEquals(1L, dto.getId());

        ClaimDocumentDTO docDto = ClaimDocumentDTO.builder().id(1L).build();
        assertEquals(1L, docDto.getId());

        ClaimInitiateRequest initReq = new ClaimInitiateRequest(1L, 1L, "desc");
        assertEquals("desc", initReq.getDescription());
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
}
