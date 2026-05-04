package com.smartsure.claims.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimDTO {
    @io.swagger.v3.oas.annotations.media.Schema(description = "Claims record ID", example = "3001")
    private Long id;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Underlying policy ID", example = "1")
    private Long policyId;
    @io.swagger.v3.oas.annotations.media.Schema(description = "User who filed the claim", example = "2")
    private Long userId;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Reason for claim", example = "Vehicle accident on highway")
    private String description;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Amount claimed", example = "50000.00")
    private Double claimedAmount;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Status (PENDING, APPROVED, REJECTED)", example = "PENDING")
    private String status;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Date when claim was submitted")
    private LocalDateTime submittedAt;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Uploaded documents as evidence")
    private List<ClaimDocumentDTO> documents;

    @io.swagger.v3.oas.annotations.media.Schema(description = "Admin remarks for the customer", example = "Claim rejected due to insufficient documentation")
    private String remarks;
}