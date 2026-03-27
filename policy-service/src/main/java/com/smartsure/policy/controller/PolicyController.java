package com.smartsure.policy.controller;

import com.smartsure.policy.dto.PolicyDTO;
import com.smartsure.policy.dto.PolicyPurchaseRequest;
import com.smartsure.policy.dto.PolicyTypeDTO;
import com.smartsure.policy.service.PolicyService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/policies")
@CrossOrigin("*")
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @PostMapping("/purchase")
    public ResponseEntity<PolicyDTO> purchasePolicy(
            @Valid @RequestBody PolicyPurchaseRequest request,
            @Parameter(hidden = true) @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(policyService.purchasePolicy(request, token));
    }

    @GetMapping("/types")
    public ResponseEntity<List<PolicyTypeDTO>> getAllPolicyTypes() {
        return ResponseEntity.ok(policyService.getAllPolicyTypes());
    }

    @PostMapping("/types")
    public ResponseEntity<PolicyTypeDTO> createPolicyType(
            @Valid @RequestBody PolicyTypeDTO request,
            @Parameter(hidden = true) @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(policyService.createPolicyType(request, token));
    }

    @PutMapping("/types/{id}")
    public ResponseEntity<PolicyTypeDTO> updatePolicyType(
            @PathVariable Long id,
            @Valid @RequestBody PolicyTypeDTO request,
            @Parameter(hidden = true) @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(policyService.updatePolicyType(id, request, token));
    }

    @DeleteMapping("/types/{id}")
    public ResponseEntity<Void> deletePolicyType(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader("Authorization") String token) {
        policyService.deletePolicyType(id, token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PolicyDTO>> getUserPolicies(@PathVariable Long userId) {
        return ResponseEntity.ok(policyService.getUserPolicies(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PolicyDTO> getPolicyById(@PathVariable Long id) {
        return ResponseEntity.ok(policyService.getPolicyById(id));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getTotalPolicies() {
        return ResponseEntity.ok(policyService.getTotalPolicies());
    }
}