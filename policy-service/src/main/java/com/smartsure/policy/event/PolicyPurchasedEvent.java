package com.smartsure.policy.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PolicyPurchasedEvent {
    private Long policyId;
    private Long userId;
    private String policyTypeName;
    private BigDecimal premiumAmount;
    private LocalDateTime timestamp;
}
