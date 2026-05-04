package com.smartsure.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    @io.swagger.v3.oas.annotations.media.Schema(description = "Total registered users", example = "1500")
    private long totalUsers;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Total policies created", example = "350")
    private long totalPolicies;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Total claims submitted", example = "45")
    private long totalClaims;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Number of pending claims", example = "12")
    private long pendingClaims;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Total revenue from all policies", example = "250000.00")
    private java.math.BigDecimal totalRevenue;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Number of approved claims", example = "30")
    private long approvedClaims;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Number of rejected claims", example = "5")
    private long rejectedClaims;
}