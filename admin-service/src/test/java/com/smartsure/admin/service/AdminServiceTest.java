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
        when(claimsServiceClient.updateClaimStatus(1L, "APPROVED"))
                .thenReturn(new ClaimDetailsDTO(1L, 10L, 20L, "Test claim", "APPROVED", Collections.emptyList()));
        when(jwtUtil.extractUsername(anyString())).thenReturn("admin@example.com");

        ClaimReviewResponse response = adminService.reviewClaim(1L, reviewRequest, "Bearer token");

        assertNotNull(response);
        assertEquals("APPROVED", response.getNewStatus());
        verify(auditLogRepository, times(1)).save(any());
    }

    @Test
    void generateReports_success() {
        when(authServiceClient.getTotalUsers()).thenReturn(100L);
        when(policyServiceClient.getTotalPolicies()).thenReturn(50L);
        when(claimsServiceClient.getTotalClaims()).thenReturn(20L);
        when(claimsServiceClient.getPendingClaimsCount()).thenReturn(5L);

        ReportDTO report = adminService.generateReports();

        assertEquals(100L, report.getTotalUsers());
        assertEquals(50L, report.getTotalPolicies());
        assertEquals(20L, report.getTotalClaims());
        assertEquals(5L, report.getPendingClaims());
    }

    @Test
    void getAllUsers_success() {
        when(authServiceClient.getAllUsers()).thenReturn(List.of(
                new UserSummaryDTO(1L, "John", "john@example.com", "999", "Addr", "CUSTOMER", "ACTIVE")
        ));

        List<UserSummaryDTO> users = adminService.getAllUsers();

        assertEquals(1, users.size());
        assertEquals("John", users.get(0).getName());
    }
}