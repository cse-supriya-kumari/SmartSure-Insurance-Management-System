package com.smartsure.claims.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartsure.claims.dto.ClaimDTO;
import com.smartsure.claims.dto.ClaimInitiateRequest;
import com.smartsure.claims.service.ClaimService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClaimController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class ClaimControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClaimService claimService;

    @Test
    void initiateClaim_success() throws Exception {
        ClaimInitiateRequest request = new ClaimInitiateRequest(5L, 1L, "Hospitalization", 10000.0);

        ClaimDTO response = ClaimDTO.builder()
                .id(1L)
                .policyId(5L)
                .userId(1L)
                .description("Hospitalization")
                .claimedAmount(10000.0)
                .status("SUBMITTED")
                .submittedAt(LocalDateTime.now())
                .documents(Collections.emptyList())
                .build();

        when(claimService.initiateClaim(any(ClaimInitiateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/claims/initiate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUBMITTED"));
    }

    @Test
    void getClaimStatus_success() throws Exception {
        ClaimDTO response = ClaimDTO.builder()
                .id(1L)
                .policyId(5L)
                .userId(1L)
                .description("Hospitalization")
                .claimedAmount(10000.0)
                .status("SUBMITTED")
                .submittedAt(LocalDateTime.now())
                .documents(Collections.emptyList())
                .build();

        when(claimService.getClaimStatus(1L)).thenReturn(response);

        mockMvc.perform(get("/api/claims/status/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void downloadDocument_success() throws Exception {
        org.springframework.core.io.Resource resource = new org.springframework.core.io.ByteArrayResource("test content".getBytes());
        com.smartsure.claims.entity.ClaimDocument doc = new com.smartsure.claims.entity.ClaimDocument();
        doc.setFileName("test.jpg");
        doc.setFileType("image/jpeg");

        when(claimService.downloadDocument(1L)).thenReturn(resource);
        when(claimService.getDocumentEntity(1L)).thenReturn(doc);

        mockMvc.perform(get("/api/claims/documents/download/1"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "inline; filename=\"test.jpg\""))
                .andExpect(content().contentType("image/jpeg"));
    }

    @Test
    void updateClaimStatus_success() throws Exception {
        ClaimDTO response = ClaimDTO.builder()
                .id(1L)
                .status("APPROVED")
                .remarks("Verified")
                .build();

        when(claimService.updateClaimStatus(eq(1L), eq("APPROVED"), eq("Verified"))).thenReturn(response);

        mockMvc.perform(put("/api/claims/1/status")
                        .param("status", "APPROVED")
                        .param("remarks", "Verified"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.remarks").value("Verified"));
    }
}