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

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClaimController.class)
class ClaimControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClaimService claimService;

    @Test
    void initiateClaim_success() throws Exception {
        ClaimInitiateRequest request = new ClaimInitiateRequest(5L, 1L, "Hospitalization");

        ClaimDTO response = ClaimDTO.builder()
                .id(1L)
                .policyId(5L)
                .userId(1L)
                .description("Hospitalization")
                .status("SUBMITTED")
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
                .status("SUBMITTED")
                .documents(Collections.emptyList())
                .build();

        when(claimService.getClaimStatus(1L)).thenReturn(response);

        mockMvc.perform(get("/api/claims/status/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}