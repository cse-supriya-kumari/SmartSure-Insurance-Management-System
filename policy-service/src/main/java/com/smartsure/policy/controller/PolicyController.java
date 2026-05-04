package com.smartsure.policy.controller;

import com.smartsure.policy.dto.PolicyDTO;
import com.smartsure.policy.dto.PolicyPurchaseRequest;
import com.smartsure.policy.dto.PolicyTypeDTO;
import com.smartsure.policy.service.PolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/policies")
@CrossOrigin("*")
@Tag(name = "Policy Management", description = "Endpoints for browsing, purchasing, and managing insurance policies")
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @Operation(summary = "Purchase a Policy", description = "Create a new policy subscription for a user")
    @ApiResponse(responseCode = "200", description = "Policy purchased successfully", 
        content = @Content(schema = @Schema(implementation = PolicyDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid purchase request")
    @PostMapping("/purchase")
    public ResponseEntity<PolicyDTO> purchasePolicy(
            @Valid @RequestBody PolicyPurchaseRequest request,
            @Parameter(hidden = true) @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(policyService.purchasePolicy(request, token));
    }

    @Operation(summary = "List Policy Types", description = "Get all available insurance products")
    @GetMapping("/types")
    public ResponseEntity<List<PolicyTypeDTO>> getAllPolicyTypes() {
        return ResponseEntity.ok(policyService.getAllPolicyTypes());
    }

    @Operation(summary = "Get Policy Type by ID", description = "Get details of a specific insurance product")
    @GetMapping("/types/{id}")
    public ResponseEntity<PolicyTypeDTO> getPolicyTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(policyService.getPolicyTypeById(id));
    }

    @Operation(summary = "Create Policy Type", description = "Add a new insurance product (Admin only)")
    @PostMapping("/types")
    public ResponseEntity<PolicyTypeDTO> createPolicyType(
            @Valid @RequestBody PolicyTypeDTO request,
            @Parameter(hidden = true) @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(policyService.createPolicyType(request, token));
    }

    @Operation(summary = "Update Policy Type", description = "Modify an existing insurance product (Admin only)")
    @PutMapping("/types/{id}")
    public ResponseEntity<PolicyTypeDTO> updatePolicyType(
            @PathVariable Long id,
            @Valid @RequestBody PolicyTypeDTO request,
            @Parameter(hidden = true) @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(policyService.updatePolicyType(id, request, token));
    }

    @Operation(summary = "Delete Policy Type", description = "Remove an insurance product from the catalog (Admin only)")
    @DeleteMapping("/types/{id}")
    public ResponseEntity<Void> deletePolicyType(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader("Authorization") String token) {
        policyService.deletePolicyType(id, token);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get User Policies", description = "Retrieve all policies belonging to a specific user")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PolicyDTO>> getUserPolicies(@PathVariable Long userId) {
        return ResponseEntity.ok(policyService.getUserPolicies(userId));
    }

    @Operation(summary = "Get Policy by ID", description = "Retrieve full details of a specific policy by its ID")
    @ApiResponse(responseCode = "200", description = "Policy found",
        content = @Content(schema = @Schema(implementation = PolicyDTO.class)))
    @ApiResponse(responseCode = "404", description = "Policy not found")
    @GetMapping("/{id}")
    public ResponseEntity<PolicyDTO> getPolicyById(@PathVariable Long id) {
        return ResponseEntity.ok(policyService.getPolicyById(id));
    }

    @Operation(summary = "Get Policy Count", description = "Get total number of policies in the system")
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalPolicies() {
        return ResponseEntity.ok(policyService.getTotalPolicies());
    }

    @Operation(summary = "Get Total Revenue", description = "Get sum of all policy premium amounts")
    @GetMapping("/revenue")
    public ResponseEntity<java.math.BigDecimal> getTotalRevenue() {
        return ResponseEntity.ok(policyService.getTotalRevenue());
    }
}