package com.smartsure.claims.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimDocumentDTO {
    private Long id;
    private String fileName;
    private String fileType;
    private String filePath;
}