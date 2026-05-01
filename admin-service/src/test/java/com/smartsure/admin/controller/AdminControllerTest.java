package com.smartsure.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartsure.admin.config.SecurityConfig;
import com.smartsure.admin.dto.ClaimReviewRequest;
import com.smartsure.admin.dto.ClaimReviewResponse;
import com.smartsure.admin.service.AdminService;
import com.smartsure.admin.util.ClaimStatus;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.smartsure.admin.security.JwtUtil;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@Import(SecurityConfig.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdminService adminService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void reviewClaim_success() throws Exception {
        ClaimReviewRequest request = new ClaimReviewRequest();
        request.setStatus(ClaimStatus.APPROVED);
        request.setRemarks("Verified");

        Mockito.doNothing().when(jwtUtil).validateToken("valid-token");
        Mockito.when(jwtUtil.extractAllClaims("valid-token")).thenReturn(
                io.jsonwebtoken.Jwts.claims().subject("admin@example.com").add("role", "ADMIN").build()
        );
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
}