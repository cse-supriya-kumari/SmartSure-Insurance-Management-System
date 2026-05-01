package com.smartsure.policy.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyTypeDTO {
    @io.swagger.v3.oas.annotations.media.Schema(description = "Internal ID of the policy type", example = "101")
    private Long id;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Name of the insurance product", example = "Health Premium")
    private String name;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Plan coverage details", example = "Comprehensive health coverage including OPD")
    private String description;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Base monthly premium", example = "500.00")
    private BigDecimal basePremium;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Maximum coverage limit", example = "500000.00")
    private BigDecimal coverageAmount;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Policy duration", example = "12")
    private Integer durationMonths;
}