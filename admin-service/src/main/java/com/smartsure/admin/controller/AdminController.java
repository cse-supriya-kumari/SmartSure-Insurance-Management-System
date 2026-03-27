package com.smartsure.admin.controller;

import com.smartsure.admin.dto.*;
import com.smartsure.admin.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/claims/{claimId}/review")
    public ResponseEntity<ClaimReviewResponse> reviewClaim(
            @PathVariable Long claimId,
            @Valid @RequestBody ClaimReviewRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(adminService.reviewClaim(claimId, request, authorizationHeader));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/reports")
    public ResponseEntity<ReportDTO> getReports() {
        return ResponseEntity.ok(adminService.generateReports());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/reports/dashboard")
    public ResponseEntity<DashboardReportDTO> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboardReport());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<UserSummaryDTO>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/{id}")
    public ResponseEntity<UserSummaryDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{id}/status")
    public ResponseEntity<UserSummaryDTO> updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody UserStatusUpdateRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(adminService.updateUserStatus(id, request, authorizationHeader));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/claims/pending")
    public ResponseEntity<List<ClaimDetailsDTO>> getPendingClaims() {
        return ResponseEntity.ok(adminService.getPendingClaims());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/claims/{claimId}")
    public ResponseEntity<ClaimDetailsDTO> getClaimById(@PathVariable Long claimId) {
        return ResponseEntity.ok(adminService.getClaimById(claimId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/claims/{claimId}/documents")
    public ResponseEntity<List<ClaimDocumentDTO>> getClaimDocuments(@PathVariable Long claimId) {
        return ResponseEntity.ok(adminService.getClaimDocuments(claimId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/policies")
    public ResponseEntity<PolicyTypeDTO> createPolicyProduct(@Valid @RequestBody PolicyTypeDTO request) {
        return ResponseEntity.ok(adminService.createPolicyProduct(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/policies/{id}")
    public ResponseEntity<PolicyTypeDTO> updatePolicyProduct(
            @PathVariable Long id, 
            @Valid @RequestBody PolicyTypeDTO request) {
        return ResponseEntity.ok(adminService.updatePolicyProduct(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/policies/{id}")
    public ResponseEntity<Void> deletePolicyProduct(@PathVariable Long id) {
        adminService.deletePolicyProduct(id);
        return ResponseEntity.ok().build();
    }
}