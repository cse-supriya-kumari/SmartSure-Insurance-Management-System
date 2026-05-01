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
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/claims")
@CrossOrigin("*")
@Tag(name = "Claims Management", description = "Endpoints for initiating, documenting, and tracking insurance claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @Operation(summary = "Initiate a Claim", description = "File a new insurance claim for an active policy")
    @ApiResponse(responseCode = "200", description = "Claim initiated successfully", 
        content = @Content(schema = @Schema(implementation = ClaimDTO.class)))
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

    @Operation(summary = "Get Claim Status", description = "Retrieve current status and details of a claim")
    @GetMapping("/status/{claimId}")
    public ResponseEntity<ClaimDTO> getClaimStatus(@PathVariable Long claimId) {
        return ResponseEntity.ok(claimService.getClaimStatus(claimId));
    }

    @Operation(summary = "Get User Claims", description = "Retrieve all claims submitted by a specific user")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ClaimDTO>> getUserClaims(@PathVariable Long userId) {
        return ResponseEntity.ok(claimService.getUserClaims(userId));
    }

    @Operation(summary = "Update Claim Status", description = "Update the status of a claim (e.g. from PENDING to APPROVED)")
    @PutMapping("/{claimId}/status")
    public ResponseEntity<ClaimDTO> updateClaimStatus(
            @PathVariable Long claimId,
            @RequestParam("status") String status) {
        return ResponseEntity.ok(claimService.updateClaimStatus(claimId, status));
    }

    @Operation(summary = "Get Total Claims", description = "Get total count of claims in the system")
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalClaims() {
        return ResponseEntity.ok(claimService.getTotalClaims());
    }

    @Operation(summary = "Get Pending Claims Count", description = "Get count of claims with PENDING status")
    @GetMapping("/count/pending")
    public ResponseEntity<Long> getPendingClaimsCount() {
        return ResponseEntity.ok(claimService.getPendingClaimsCount());
    }

    @Operation(summary = "List Pending Claims", description = "Get all claims that are currently in PENDING status")
    @GetMapping("/pending")
    public ResponseEntity<List<ClaimDTO>> getPendingClaims() {
        return ResponseEntity.ok(claimService.getPendingClaims());
    }

    @Operation(summary = "Get Claim Documents", description = "Retrieve all evidence files associated with a claim")
    @GetMapping("/{claimId}/documents")
    public ResponseEntity<List<ClaimDocumentDTO>> getClaimDocuments(@PathVariable Long claimId) {
        return ResponseEntity.ok(claimService.getClaimDocuments(claimId));
    }
}