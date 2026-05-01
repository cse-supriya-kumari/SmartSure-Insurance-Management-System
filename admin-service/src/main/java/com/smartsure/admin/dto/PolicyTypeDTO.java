package com.smartsure.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyTypeDTO {
    @Schema(description = "Internal ID of the policy type", example = "101")
    private Long id;
    @Schema(description = "Name of the insurance product", example = "Health Premium")
    private String name;
    @Schema(description = "Plan coverage details", example = "Comprehensive health coverage including OPD")
    private String description;
    @Schema(description = "Base total premium for the full duration", example = "12000.00")
    private BigDecimal basePremium;
    @Schema(description = "Maximum coverage limit", example = "300000.00")
    private BigDecimal coverageAmount;
    @Schema(description = "Policy duration in months", example = "12")
    private Integer durationMonths;
}
