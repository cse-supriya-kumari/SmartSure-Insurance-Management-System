package com.smartsure.admin.client;

import com.smartsure.admin.dto.ClaimDetailsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "claims-service", url = "${CLAIMS_SERVICE_URL:http://localhost:8083}", path = "/api/claims")
public interface ClaimsServiceClient {

    @PutMapping("/{claimId}/status")
    ClaimDetailsDTO updateClaimStatus(
            @PathVariable("claimId") Long claimId, 
            @RequestParam("status") String status, 
            @RequestParam(value = "remarks", required = false) String remarks,
            @RequestHeader("Authorization") String token);

    @GetMapping("/count")
    Long getTotalClaims(@RequestHeader("Authorization") String token);

    @GetMapping("/count/pending")
    Long getPendingClaimsCount(@RequestHeader("Authorization") String token);

    @GetMapping("/count/approved")
    Long getApprovedClaimsCount(@RequestHeader("Authorization") String token);

    @GetMapping("/count/rejected")
    Long getRejectedClaimsCount(@RequestHeader("Authorization") String token);

    @GetMapping("/status/{claimId}")
    ClaimDetailsDTO getClaimById(@PathVariable("claimId") Long claimId, @RequestHeader("Authorization") String token);

    @GetMapping("/pending")
    List<ClaimDetailsDTO> getPendingClaims(@RequestHeader("Authorization") String token);

    @GetMapping("/{claimId}/documents")
    List<com.smartsure.admin.dto.ClaimDocumentDTO> getClaimDocuments(@PathVariable("claimId") Long claimId, @RequestHeader("Authorization") String token);
}