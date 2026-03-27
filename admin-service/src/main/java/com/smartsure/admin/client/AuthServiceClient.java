package com.smartsure.admin.client;

import com.smartsure.admin.dto.UserSummaryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "auth-service", url = "${AUTH_SERVICE_URL:http://localhost:8081}", path = "/api/auth")
public interface AuthServiceClient {

    @GetMapping("/users/count")
    Long getTotalUsers();

    @GetMapping("/users")
    List<UserSummaryDTO> getAllUsers();

    @GetMapping("/users/{id}")
    UserSummaryDTO getUserById(@PathVariable("id") Long id);

    @PutMapping("/users/{id}/status")
    UserSummaryDTO updateUserStatus(@PathVariable("id") Long id, @RequestBody com.smartsure.admin.dto.UserStatusUpdateRequest request);
}