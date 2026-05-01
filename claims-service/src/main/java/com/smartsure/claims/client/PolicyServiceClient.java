package com.smartsure.claims.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "policy-service", url = "${POLICY_SERVICE_URL:http://localhost:8082}", path = "/api/policies")
public interface PolicyServiceClient {

    @GetMapping("/{id}")
    Object getPolicyById(@PathVariable("id") Long id);
}