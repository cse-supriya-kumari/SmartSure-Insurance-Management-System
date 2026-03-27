package com.smartsure.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimDetailsDTO {
    private Long id;
    private Long policyId;
    private Long userId;
    private String description;
    private String status;
    private List<ClaimDocumentDTO> documents;
}