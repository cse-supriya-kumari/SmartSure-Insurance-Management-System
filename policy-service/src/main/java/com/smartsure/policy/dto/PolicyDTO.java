package com.smartsure.policy.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyDTO {
    @io.swagger.v3.oas.annotations.media.Schema(description = "Policy ID", example = "1")
    private Long id;
    @io.swagger.v3.oas.annotations.media.Schema(description = "User ID and Owner", example = "2")
    private Long userId;
    @io.swagger.v3.oas.annotations.media.Schema(description = "ID of associated Policy Type", example = "101")
    private Long policyTypeId;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Name of policy type", example = "Health")
    private String policyTypeName;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Start date", example = "2024-03-30")
    private LocalDate startDate;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Expiry date", example = "2025-03-30")
    private LocalDate endDate;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Total premium", example = "12000.50")
    private BigDecimal premiumAmount;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Status (ACTIVE, EXPIRED)", example = "ACTIVE")
    private String status;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Premium breakdown")
    private List<PremiumDTO> premiums;
}