package com.smartsure.policy.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PremiumDTO {
    @io.swagger.v3.oas.annotations.media.Schema(description = "Premium record ID", example = "5001")
    private Long id;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Payment amount", example = "1000.00")
    private BigDecimal amount;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Scheduled payment date", example = "2024-04-15")
    private LocalDate dueDate;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Actual date of payment", example = "2024-04-10")
    private LocalDate paidDate;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Payment status (PENDING, PAID)", example = "PAID")
    private String status;
}