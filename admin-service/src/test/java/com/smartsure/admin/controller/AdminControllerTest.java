package com.smartsure.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartsure.admin.dto.*;
import com.smartsure.admin.service.AdminService;
import com.smartsure.admin.util.ClaimStatus;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.smartsure.admin.security.JwtUtil;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdminService adminService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        // No security filters enabled, so no need to stub jwtUtil globally
    }

    @Test
    void reviewClaim_success() throws Exception {
        ClaimReviewRequest request = new ClaimReviewRequest();
        request.setStatus(ClaimStatus.APPROVED);
        request.setRemarks("Verified");

        Mockito.when(adminService.reviewClaim(Mockito.eq(1L), Mockito.any(), Mockito.anyString()))
                .thenReturn(new ClaimReviewResponse(1L, "APPROVED", "Claim reviewed successfully"));

        mockMvc.perform(put("/api/admin/claims/1/review")
                        .with(user("admin").roles("ADMIN"))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newStatus").value("APPROVED"));
    }

    @Test
    void getReports_success() throws Exception {
        Mockito.when(adminService.generateReports(anyString()))
                .thenReturn(ReportDTO.builder()
                        .totalUsers(100L)
                        .totalPolicies(50L)
                        .totalClaims(20L)
                        .pendingClaims(5L)
                        .totalRevenue(java.math.BigDecimal.ZERO)
                        .approvedClaims(10L)
                        .rejectedClaims(5L)
                        .build());

        mockMvc.perform(get("/api/admin/reports")
                        .with(user("admin").roles("ADMIN"))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(100));
    }

    @Test
    void getAllUsers_success() throws Exception {
        Mockito.when(adminService.getAllUsers(anyString()))
                .thenReturn(java.util.List.of(new UserSummaryDTO(1L, "John", "john@example.com", "999", "Addr", "CUSTOMER", "ACTIVE")));

        mockMvc.perform(get("/api/admin/users")
                        .with(user("admin").roles("ADMIN"))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John"));
    }

    @Test
    void getPendingClaims_success() throws Exception {
        Mockito.when(adminService.getPendingClaims(anyString()))
                .thenReturn(java.util.List.of(new ClaimDetailsDTO(1L, 10L, 20L, "Desc", 100.0, "PENDING", java.time.LocalDateTime.now(), java.util.Collections.emptyList(), null)));

        mockMvc.perform(get("/api/admin/claims/pending")
                        .with(user("admin").roles("ADMIN"))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }
}