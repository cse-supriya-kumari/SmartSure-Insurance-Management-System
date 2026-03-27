package com.smartsure.admin.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "policy-service", url = "${POLICY_SERVICE_URL:http://localhost:8082}", path = "/api/policies")
public interface PolicyServiceClient {

    @GetMapping("/count")
    Long getTotalPolicies();

    @org.springframework.web.bind.annotation.PostMapping("/types")
    com.smartsure.admin.dto.PolicyTypeDTO createPolicyType(@org.springframework.web.bind.annotation.RequestBody com.smartsure.admin.dto.PolicyTypeDTO request);

    @org.springframework.web.bind.annotation.PutMapping("/types/{id}")
    com.smartsure.admin.dto.PolicyTypeDTO updatePolicyType(@org.springframework.web.bind.annotation.PathVariable("id") Long id, @org.springframework.web.bind.annotation.RequestBody com.smartsure.admin.dto.PolicyTypeDTO request);

    @org.springframework.web.bind.annotation.DeleteMapping("/types/{id}")
    void deletePolicyType(@org.springframework.web.bind.annotation.PathVariable("id") Long id);
}