package com.smartsure.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardReportDTO {
    @io.swagger.v3.oas.annotations.media.Schema(description = "Total registered users", example = "1500")
    private long totalUsers;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Total active policies", example = "350")
    private long totalPolicies;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Total claims filed", example = "45")
    private long totalClaims;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Claims pending review", example = "12")
    private long pendingClaims;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Overall system status", example = "HEALTHY")
    private String status;
}