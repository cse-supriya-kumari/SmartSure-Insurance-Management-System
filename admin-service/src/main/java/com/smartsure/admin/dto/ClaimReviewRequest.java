package com.smartsure.admin.dto;

import com.smartsure.admin.util.ClaimStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClaimReviewRequest {

    @io.swagger.v3.oas.annotations.media.Schema(description = "New claim status (APPROVED, REJECTED)", example = "APPROVED")
    @NotNull(message = "Status is required")
    private ClaimStatus status;

    @io.swagger.v3.oas.annotations.media.Schema(description = "Admin remarks or reason for decision", example = "All documents verified and coverage matches.")
    private String remarks;
}