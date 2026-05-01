package com.smartsure.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimDocumentDTO {
    private Long id;
    private String fileName;
    private String fileType;
    private String filePath;
}