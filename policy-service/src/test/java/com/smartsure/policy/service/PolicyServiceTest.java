package com.smartsure.policy.service;

import com.smartsure.policy.client.AuthServiceClient;
import com.smartsure.policy.dto.PolicyDTO;
import com.smartsure.policy.dto.PolicyPurchaseRequest;
import com.smartsure.policy.dto.UserSummaryDTO;
import com.smartsure.policy.entity.*;
import com.smartsure.policy.repository.PolicyRepository;
import com.smartsure.policy.repository.PolicyTypeRepository;
import com.smartsure.policy.repository.PremiumRepository;
import com.smartsure.policy.exception.ResourceNotFoundException;
import com.smartsure.policy.exception.UnauthorizedException;
import com.smartsure.policy.event.PolicyEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PolicyServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private PolicyTypeRepository policyTypeRepository;

    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private AuthServiceClient authServiceClient;

    @Mock
    private PolicyEventPublisher policyEventPublisher;

    @Mock
    private com.smartsure.policy.mapper.PolicyMapper policyMapper;

    @InjectMocks
    private PolicyService policyService;

    private PolicyType healthType;

    @BeforeEach
    void setUp() {
        healthType = PolicyType.builder()
                .id(1L)
                .name("Health Insurance")
                .description("Medical coverage")
                .basePremium(new BigDecimal("12000"))
                .coverageAmount(new BigDecimal("300000"))
                .durationMonths(12)
                .build();
    }

    @Test
    void testPurchasePolicy_success() {
        PolicyPurchaseRequest request = new PolicyPurchaseRequest(10L, 1L);

        UserSummaryDTO user = UserSummaryDTO.builder().role("CUSTOMER").build();
        when(authServiceClient.validateToken("ValidToken")).thenReturn(user);
        when(policyTypeRepository.findById(1L)).thenReturn(Optional.of(healthType));

        Policy savedPolicy = Policy.builder()
                .id(100L)
                .userId(10L)
                .policyType(healthType)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(12))
                .premiumAmount(new BigDecimal("12000"))
                .status(PolicyStatus.ACTIVE)
                .build();

        when(policyRepository.save(any(Policy.class))).thenReturn(savedPolicy);

        Premium premium = Premium.builder()
                .id(1L)
                .policy(savedPolicy)
                .amount(new BigDecimal("1000"))
                .dueDate(LocalDate.now().plusMonths(1))
                .status(PremiumStatus.PENDING)
                .build();

        when(premiumRepository.saveAll(anyList())).thenReturn(List.of(premium));
        PolicyDTO dto = PolicyDTO.builder().userId(10L).status("ACTIVE").build();
        when(policyMapper.toPolicyDTO(any(Policy.class), anyList())).thenReturn(dto);

        PolicyDTO result = policyService.purchasePolicy(request, "ValidToken");

        assertNotNull(result);
        assertEquals(10L, result.getUserId());
        assertEquals("ACTIVE", result.getStatus());
        verify(premiumRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testPurchasePolicy_invalidToken() {
        PolicyPurchaseRequest request = new PolicyPurchaseRequest(10L, 1L);

        when(authServiceClient.validateToken("InvalidToken")).thenReturn(null);

        UnauthorizedException ex = assertThrows(UnauthorizedException.class,
                () -> policyService.purchasePolicy(request, "InvalidToken"));

        assertEquals("Only customers can purchase policies", ex.getMessage());
    }

    @Test
    void testGetPolicyById_success() {
        Policy policy = Policy.builder()
                .id(100L)
                .userId(10L)
                .policyType(healthType)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(12))
                .premiumAmount(new BigDecimal("12000"))
                .status(PolicyStatus.ACTIVE)
                .build();

        when(policyRepository.findById(100L)).thenReturn(Optional.of(policy));
        when(premiumRepository.findByPolicyId(100L)).thenReturn(List.of());
        PolicyDTO dto = PolicyDTO.builder().id(100L).policyTypeName("Health Insurance").build();
        when(policyMapper.toPolicyDTO(any(Policy.class), anyList())).thenReturn(dto);

        PolicyDTO result = policyService.getPolicyById(100L);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("Health Insurance", result.getPolicyTypeName());
    }
}