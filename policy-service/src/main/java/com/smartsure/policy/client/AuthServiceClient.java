package com.smartsure.policy.client;

import com.smartsure.policy.dto.UserSummaryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service", url = "${AUTH_SERVICE_URL:http://localhost:8081}", path = "/api/auth")
public interface AuthServiceClient {

    @GetMapping("/validate")
    UserSummaryDTO validateToken(@RequestHeader("Authorization") String token);
}