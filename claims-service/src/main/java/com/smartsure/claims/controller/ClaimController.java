package com.smartsure.claims.controller;

import com.smartsure.claims.dto.*;
import com.smartsure.claims.service.ClaimService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/claims")
@CrossOrigin("*")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping("/initiate")
    public ResponseEntity<ClaimDTO> initiateClaim(@Valid @RequestBody ClaimInitiateRequest request) {
        return ResponseEntity.ok(claimService.initiateClaim(request));
    }

    @Operation(summary = "Upload claim document", description = "Upload a file for a specific claim. Max size 10MB.")
    @ApiResponse(responseCode = "200", description = "Document uploaded successfully")
    @PostMapping(value = "/{claimId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ClaimDocumentDTO> uploadDocument(
            @Parameter(description = "ID of the claim") @PathVariable Long claimId,
            @Parameter(description = "File to upload", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(type = "string", format = "binary"))) 
            @RequestPart("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(claimService.uploadDocument(claimId, file));
    }

    @GetMapping("/status/{claimId}")
    public ResponseEntity<ClaimDTO> getClaimStatus(@PathVariable Long claimId) {
        return ResponseEntity.ok(claimService.getClaimStatus(claimId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ClaimDTO>> getUserClaims(@PathVariable Long userId) {
        return ResponseEntity.ok(claimService.getUserClaims(userId));
    }

    @PutMapping("/{claimId}/status")
    public ResponseEntity<ClaimDTO> updateClaimStatus(
            @PathVariable Long claimId,
            @RequestParam("status") String status) {
        return ResponseEntity.ok(claimService.updateClaimStatus(claimId, status));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getTotalClaims() {
        return ResponseEntity.ok(claimService.getTotalClaims());
    }

    @GetMapping("/count/pending")
    public ResponseEntity<Long> getPendingClaimsCount() {
        return ResponseEntity.ok(claimService.getPendingClaimsCount());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ClaimDTO>> getPendingClaims() {
        return ResponseEntity.ok(claimService.getPendingClaims());
    }

    @GetMapping("/{claimId}/documents")
    public ResponseEntity<List<ClaimDocumentDTO>> getClaimDocuments(@PathVariable Long claimId) {
        return ResponseEntity.ok(claimService.getClaimDocuments(claimId));
    }
}