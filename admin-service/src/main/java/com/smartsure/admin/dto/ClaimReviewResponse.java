package com.smartsure.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimReviewResponse {
    private Long claimId;
    private String newStatus;
    private String message;
}