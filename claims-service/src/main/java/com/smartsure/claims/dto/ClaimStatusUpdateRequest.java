package com.smartsure.claims.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClaimStatusUpdateRequest {

    @NotBlank(message = "Status is required")
    private String status;
}