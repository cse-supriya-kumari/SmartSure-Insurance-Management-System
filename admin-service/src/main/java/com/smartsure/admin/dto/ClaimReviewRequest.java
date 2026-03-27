package com.smartsure.admin.dto;

import com.smartsure.admin.util.ClaimStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClaimReviewRequest {

    @NotNull(message = "Status is required")
    private ClaimStatus status;

    private String remarks;
}