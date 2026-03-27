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
    private Long id;
    private Long userId;
    private Long policyTypeId;
    private String policyTypeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal premiumAmount;
    private String status;
    private List<PremiumDTO> premiums;
}