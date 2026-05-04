package com.smartsure.admin.service;

import com.smartsure.admin.client.AuthServiceClient;
import com.smartsure.admin.client.ClaimsServiceClient;
import com.smartsure.admin.client.PolicyServiceClient;
import com.smartsure.admin.dto.*;
import com.smartsure.admin.repository.AdminAuditLogRepository;
import com.smartsure.admin.security.JwtUtil;
import com.smartsure.admin.util.ClaimStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private ClaimsServiceClient claimsServiceClient;

    @Mock
    private PolicyServiceClient policyServiceClient;

    @Mock
    private AuthServiceClient authServiceClient;

    @Mock
    private AdminAuditLogRepository auditLogRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AdminService adminService;

    private ClaimReviewRequest reviewRequest;

    @BeforeEach
    void setUp() {
        reviewRequest = new ClaimReviewRequest();
        reviewRequest.setStatus(ClaimStatus.APPROVED);
        reviewRequest.setRemarks("Documents verified");
    }
    @Test
    void reviewClaim_success() {
        when(claimsServiceClient.getClaimById(anyLong(), anyString()))
                .thenReturn(new ClaimDetailsDTO(1L, 10L, 20L, "Test claim", 500.0, "PENDING", LocalDateTime.now(), Collections.emptyList(), null));
        when(claimsServiceClient.updateClaimStatus(anyLong(), anyString(), anyString(), anyString()))
                .thenReturn(new ClaimDetailsDTO(1L, 10L, 20L, "Test claim", 500.0, "APPROVED", LocalDateTime.now(), Collections.emptyList(), "Documents verified"));
        when(jwtUtil.extractUsername(anyString())).thenReturn("admin@example.com");

        ClaimReviewResponse response = adminService.reviewClaim(1L, reviewRequest, "Bearer token");

        assertNotNull(response);
        assertEquals("APPROVED", response.getNewStatus());
        verify(auditLogRepository, times(1)).save(any());
    }

    @Test
    void generateReports_success() {
        when(authServiceClient.getTotalUsers(anyString())).thenReturn(100L);
        when(policyServiceClient.getTotalPolicies()).thenReturn(50L);
        when(claimsServiceClient.getTotalClaims(anyString())).thenReturn(20L);
        when(claimsServiceClient.getPendingClaimsCount(anyString())).thenReturn(5L);
        when(claimsServiceClient.getApprovedClaimsCount(anyString())).thenReturn(10L);
        when(claimsServiceClient.getRejectedClaimsCount(anyString())).thenReturn(5L);
        when(policyServiceClient.getTotalRevenue()).thenReturn(java.math.BigDecimal.valueOf(1000));

        ReportDTO report = adminService.generateReports("Bearer token");

        assertEquals(100L, report.getTotalUsers());
        assertEquals(50L, report.getTotalPolicies());
        assertEquals(20L, report.getTotalClaims());
        assertEquals(5L, report.getPendingClaims());
    }

    @Test
    void getAllUsers_success() {
        when(authServiceClient.getAllUsers(anyString())).thenReturn(List.of(
                new UserSummaryDTO(1L, "John", "john@example.com", "999", "Addr", "CUSTOMER", "ACTIVE")
        ));

        List<UserSummaryDTO> users = adminService.getAllUsers("Bearer token");

        assertEquals(1, users.size());
        assertEquals("John", users.get(0).getName());
    }
}