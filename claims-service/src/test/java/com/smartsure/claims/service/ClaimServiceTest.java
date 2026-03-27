package com.smartsure.claims.service;

import com.smartsure.claims.client.PolicyServiceClient;
import com.smartsure.claims.dto.ClaimDTO;
import com.smartsure.claims.dto.ClaimInitiateRequest;
import com.smartsure.claims.entity.Claim;
import com.smartsure.claims.entity.ClaimStatus;
import com.smartsure.claims.repository.ClaimDocumentRepository;
import com.smartsure.claims.repository.ClaimRepository;
import com.smartsure.claims.util.FileStorageUtil;
import com.smartsure.claims.event.ClaimEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClaimServiceTest {

    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private ClaimDocumentRepository documentRepository;

    @Mock
    private PolicyServiceClient policyServiceClient;

    @Mock
    private FileStorageUtil fileStorageUtil;

    @Mock
    private ClaimEventPublisher claimEventPublisher;

    @InjectMocks
    private ClaimService claimService;

    private Claim savedClaim;

    @BeforeEach
    void setUp() {
        savedClaim = Claim.builder()
                .id(1L)
                .policyId(5L)
                .userId(1L)
                .description("Hospitalization claim")
                .status(ClaimStatus.SUBMITTED)
                .build();
    }

    @Test
    void testInitiateClaim_success() {
        ClaimInitiateRequest request = new ClaimInitiateRequest(5L, 1L, "Health claim for hospitalization");

        when(policyServiceClient.getPolicyById(5L)).thenReturn(new Object());
        when(claimRepository.save(any(Claim.class))).thenReturn(savedClaim);
        when(documentRepository.findByClaim_Id(1L)).thenReturn(Collections.emptyList());

        ClaimDTO result = claimService.initiateClaim(request);

        assertNotNull(result);
        assertEquals("SUBMITTED", result.getStatus());
        assertEquals(5L, result.getPolicyId());
        verify(claimRepository, times(1)).save(any(Claim.class));
    }

    @Test
    void testInitiateClaim_invalidPolicy() {
        ClaimInitiateRequest request = new ClaimInitiateRequest(99L, 1L, "Health claim");

        when(policyServiceClient.getPolicyById(99L)).thenThrow(new RuntimeException("Not found"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> claimService.initiateClaim(request));
        assertEquals("Invalid Policy ID", ex.getMessage());
    }

    @Test
    void testGetClaimStatus_success() {
        when(claimRepository.findById(1L)).thenReturn(Optional.of(savedClaim));
        when(documentRepository.findByClaim_Id(1L)).thenReturn(Collections.emptyList());

        ClaimDTO result = claimService.getClaimStatus(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("SUBMITTED", result.getStatus());
    }
}