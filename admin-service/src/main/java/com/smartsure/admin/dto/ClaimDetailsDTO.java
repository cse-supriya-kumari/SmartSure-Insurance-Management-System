package com.smartsure.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimDetailsDTO {
    @io.swagger.v3.oas.annotations.media.Schema(description = "Claims record ID", example = "3001")
    private Long id;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Underlying policy ID", example = "1")
    private Long policyId;
    @io.swagger.v3.oas.annotations.media.Schema(description = "User who filed the claim", example = "2")
    private Long userId;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Reason for claim", example = "Vehicle accident on highway")
    private String description;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Current claim status", example = "PENDING")
    private String status;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Associated evidence documents")
    private List<ClaimDocumentDTO> documents;
}