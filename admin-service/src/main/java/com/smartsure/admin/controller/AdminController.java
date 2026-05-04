package com.smartsure.admin.controller;

import com.smartsure.admin.dto.*;
import com.smartsure.admin.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
@Tag(name = "Admin Operations", description = "Endpoints for administrator tasks, system reports, and claim management")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Operation(summary = "Review Claim", description = "Approve or reject a submitted insurance claim")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/claims/{claimId}/review")
    public ResponseEntity<ClaimReviewResponse> reviewClaim(
            @PathVariable Long claimId,
            @Valid @RequestBody ClaimReviewRequest request,
            @Parameter(hidden = true) @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(adminService.reviewClaim(claimId, request, authorizationHeader));
    }

    @Operation(summary = "Get System Report", description = "Generate a comprehensive status report for the system")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/reports")
    public ResponseEntity<ReportDTO> getReports(
            @Parameter(hidden = true) @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(adminService.generateReports(authorizationHeader));
    }

    @Operation(summary = "Get Dashboard Stats", description = "Fetch real-time dashboard data (cached)")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/reports/dashboard")
    public ResponseEntity<DashboardReportDTO> getDashboard(
            @Parameter(hidden = true) @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(adminService.getDashboardReport(authorizationHeader));
    }

    @Operation(summary = "List All Users", description = "Retrieve a list of all registered users in the platform")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<UserSummaryDTO>> getAllUsers(
            @Parameter(hidden = true) @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(adminService.getAllUsers(authorizationHeader));
    }

    @Operation(summary = "Get User by ID", description = "Retrieve detailed information for a particular user")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/{id}")
    public ResponseEntity<UserSummaryDTO> getUserById(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(adminService.getUserById(id, authorizationHeader));
    }

    @Operation(summary = "Update User Status", description = "Activate or deactivate user accounts")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{id}/status")
    public ResponseEntity<UserSummaryDTO> updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody UserStatusUpdateRequest request,
            @Parameter(hidden = true) @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(adminService.updateUserStatus(id, request, authorizationHeader));
    }

    @Operation(summary = "Get Pending Claims", description = "List all claims currently awaiting admin review")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/claims/pending")
    public ResponseEntity<List<ClaimDetailsDTO>> getPendingClaims(
            @Parameter(hidden = true) @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(adminService.getPendingClaims(authorizationHeader));
    }

    @Operation(summary = "Get Claim Details", description = "View full details of a specific claim")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/claims/{claimId}")
    public ResponseEntity<ClaimDetailsDTO> getClaimById(
            @PathVariable Long claimId,
            @Parameter(hidden = true) @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(adminService.getClaimById(claimId, authorizationHeader));
    }

    @Operation(summary = "Get Claim Evidence", description = "View attached documents for a specific claim")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/claims/{claimId}/documents")
    public ResponseEntity<List<ClaimDocumentDTO>> getClaimDocuments(
            @PathVariable Long claimId,
            @Parameter(hidden = true) @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(adminService.getClaimDocuments(claimId, authorizationHeader));
    }

    @Operation(summary = "Create Policy Product", description = "Define a new policy type offering")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/policies")
    public ResponseEntity<PolicyTypeDTO> createPolicyProduct(
            @Valid @RequestBody PolicyTypeDTO request,
            @Parameter(hidden = true) @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(adminService.createPolicyProduct(request, authorizationHeader));
    }

    @Operation(summary = "Update Policy Product", description = "Modify offering details for an existing policy type")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/policies/{id}")
    public ResponseEntity<PolicyTypeDTO> updatePolicyProduct(
            @PathVariable Long id,
            @Valid @RequestBody PolicyTypeDTO request,
            @Parameter(hidden = true) @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(adminService.updatePolicyProduct(id, request, authorizationHeader));
    }

    @Operation(summary = "Delete Policy Product", description = "Remove a policy offering from the system")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/policies/{id}")
    public ResponseEntity<Void> deletePolicyProduct(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        adminService.deletePolicyProduct(id, authorizationHeader);
        return ResponseEntity.ok().build();
    }
}