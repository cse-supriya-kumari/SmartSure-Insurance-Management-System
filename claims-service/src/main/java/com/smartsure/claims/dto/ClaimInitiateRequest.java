package com.smartsure.claims.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimInitiateRequest {

    @io.swagger.v3.oas.annotations.media.Schema(description = "Policy under which claim is filed", example = "1")
    @NotNull(message = "Policy ID is required")
    private Long policyId;

    @io.swagger.v3.oas.annotations.media.Schema(description = "User filing the claim", example = "2")
    @NotNull(message = "User ID is required")
    private Long userId;

    @io.swagger.v3.oas.annotations.media.Schema(description = "Reason and context for the claim", example = "Theft of electronics from residence")
    @NotBlank(message = "Description is required")
    private String description;

    @io.swagger.v3.oas.annotations.media.Schema(description = "Amount being claimed", example = "50000.00")
    @NotNull(message = "Claimed amount is required")
    private Double claimedAmount;
}