package com.smartsure.policy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartsure.policy.dto.PolicyDTO;
import com.smartsure.policy.dto.PolicyPurchaseRequest;
import com.smartsure.policy.dto.PolicyTypeDTO;
import com.smartsure.policy.service.PolicyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PolicyController.class)
class PolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PolicyService policyService;

    @Test
    void purchasePolicy_success() throws Exception {
        PolicyPurchaseRequest request = new PolicyPurchaseRequest(10L, 1L);

        PolicyDTO response = PolicyDTO.builder()
                .id(100L)
                .userId(10L)
                .policyTypeId(1L)
                .policyTypeName("Health Insurance")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(12))
                .premiumAmount(new BigDecimal("12000"))
                .status("ACTIVE")
                .premiums(Collections.emptyList())
                .build();

        when(policyService.purchasePolicy(any(PolicyPurchaseRequest.class), any(String.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/policies/purchase")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void purchasePolicy_missingHeader() throws Exception {
        PolicyPurchaseRequest request = new PolicyPurchaseRequest(10L, 1L);

        mockMvc.perform(post("/api/policies/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Required header 'Authorization' is missing or invalid"));
    }

    @Test
    void getAllPolicyTypes_success() throws Exception {
        List<PolicyTypeDTO> types = List.of(
                PolicyTypeDTO.builder()
                        .id(1L)
                        .name("Health Insurance")
                        .description("Medical coverage")
                        .basePremium(new BigDecimal("12000"))
                        .coverageAmount(new BigDecimal("300000"))
                        .durationMonths(12)
                        .build()
        );

        when(policyService.getAllPolicyTypes()).thenReturn(types);

        mockMvc.perform(get("/api/policies/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Health Insurance"));
    }
}