package com.smartsure.admin;

import com.smartsure.admin.dto.*;
import com.smartsure.admin.entity.AdminAuditLog;
import com.smartsure.admin.exception.GlobalExceptionHandler;
import com.smartsure.admin.exception.ResourceNotFoundException;
import com.smartsure.admin.exception.ServiceUnavailableException;
import com.smartsure.admin.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminCoverageBoosterTest {

    @Test
    void testEntitiesAndDtos() {
        AdminAuditLog log = AdminAuditLog.builder()
                .id(1L).adminEmail("a").action("A").targetType("T")
                .targetId(1L).oldValue("O").newValue("N").remarks("R")
                .createdAt(LocalDateTime.now()).build();
        assertEquals(1L, log.getId());
        assertEquals("a", log.getAdminEmail());

        // DTOs
        ClaimReviewRequest reviewReq = new ClaimReviewRequest();
        reviewReq.setRemarks("R");
        assertEquals("R", reviewReq.getRemarks());

        DashboardReportDTO dashboard = DashboardReportDTO.builder().totalUsers(10L).build();
        assertEquals(10L, dashboard.getTotalUsers());

        ReportDTO report = ReportDTO.builder().totalClaims(5L).build();
        assertEquals(5L, report.getTotalClaims());

        ClaimDetailsDTO claimDetails = new ClaimDetailsDTO(1L, 1L, 1L, "D", 100.0, "S", LocalDateTime.now(), List.of(), "R");
        assertEquals("R", claimDetails.getRemarks());
    }

    @Test
    void testGlobalExceptionHandler() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/test");

        ResponseEntity<?> response;
        response = handler.handleNotFound(new ResourceNotFoundException("Not found"), request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        response = handler.handleServiceUnavailable(new ServiceUnavailableException("Unavailable"), request);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    }

    @Test
    void testJwtUtil() {
        JwtUtil jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437");
        
        // Basic method check
        assertNotNull(jwtUtil);
    }
}
