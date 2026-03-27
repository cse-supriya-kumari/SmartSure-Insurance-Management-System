package com.smartsure.claims.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimDTO {
    private Long id;
    private Long policyId;
    private Long userId;
    private String description;
    private String status;
    private List<ClaimDocumentDTO> documents;
}