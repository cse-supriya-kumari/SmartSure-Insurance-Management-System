package com.smartsure.policy.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyPurchaseRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Policy Type ID is required")
    private Long policyTypeId;
}