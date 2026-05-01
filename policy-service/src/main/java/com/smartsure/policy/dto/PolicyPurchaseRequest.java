package com.smartsure.policy.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyPurchaseRequest {

    @io.swagger.v3.oas.annotations.media.Schema(description = "User ID who is purchasing the policy", example = "2")
    @NotNull(message = "User ID is required")
    private Long userId;

    @io.swagger.v3.oas.annotations.media.Schema(description = "ID of the policy type being purchased", example = "101")
    @NotNull(message = "Policy Type ID is required")
    private Long policyTypeId;
}