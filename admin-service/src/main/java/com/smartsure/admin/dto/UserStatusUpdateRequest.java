package com.smartsure.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserStatusUpdateRequest {

    @Schema(description = "New status for the user account", example = "INACTIVE",
            allowableValues = {"ACTIVE", "INACTIVE"})
    @NotBlank(message = "Status is required")
    private String status;
}