package com.smartsure.policy.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyTypeDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal basePremium;
    private BigDecimal coverageAmount;
    private Integer durationMonths;
}