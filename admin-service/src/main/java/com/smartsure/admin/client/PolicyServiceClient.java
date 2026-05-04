package com.smartsure.admin.client;

import com.smartsure.admin.dto.PolicyTypeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "policy-service", url = "${POLICY_SERVICE_URL:http://localhost:8082}", path = "/api/policies")
public interface PolicyServiceClient {

    @GetMapping("/count")
    Long getTotalPolicies();

    @GetMapping("/revenue")
    java.math.BigDecimal getTotalRevenue();

    /**
     * policy-service's createPolicyType endpoint requires an Authorization header
     * to validate that the caller is an ADMIN. We must forward it from the
     * original incoming request.
     */
    @PostMapping("/types")
    PolicyTypeDTO createPolicyType(
            @RequestBody PolicyTypeDTO request,
            @RequestHeader("Authorization") String token);

    /** Same requirement — forward the Authorization header. */
    @PutMapping("/types/{id}")
    PolicyTypeDTO updatePolicyType(
            @PathVariable("id") Long id,
            @RequestBody PolicyTypeDTO request,
            @RequestHeader("Authorization") String token);

    /** Same requirement — forward the Authorization header. */
    @DeleteMapping("/types/{id}")
    void deletePolicyType(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String token);
}