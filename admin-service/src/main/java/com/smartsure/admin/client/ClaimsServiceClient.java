package com.smartsure.admin.client;

import com.smartsure.admin.dto.ClaimDetailsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "claims-service", url = "${CLAIMS_SERVICE_URL:http://localhost:8083}", path = "/api/claims")
public interface ClaimsServiceClient {

    @PutMapping("/{claimId}/status")
    ClaimDetailsDTO updateClaimStatus(@PathVariable("claimId") Long claimId, @RequestParam("status") String status);

    @GetMapping("/count")
    Long getTotalClaims();

    @GetMapping("/count/pending")
    Long getPendingClaimsCount();

    @GetMapping("/{claimId}")
    ClaimDetailsDTO getClaimById(@PathVariable("claimId") Long claimId);

    @GetMapping("/pending")
    List<ClaimDetailsDTO> getPendingClaims();

    @GetMapping("/{claimId}/documents")
    List<com.smartsure.admin.dto.ClaimDocumentDTO> getClaimDocuments(@PathVariable("claimId") Long claimId);
}