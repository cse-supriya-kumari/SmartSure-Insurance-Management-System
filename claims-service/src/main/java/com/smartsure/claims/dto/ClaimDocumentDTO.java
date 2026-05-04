package com.smartsure.claims.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimDocumentDTO {
    @io.swagger.v3.oas.annotations.media.Schema(description = "Document ID", example = "4001")
    private Long id;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Name of the file", example = "medical_bill.pdf")
    private String fileName;
    @io.swagger.v3.oas.annotations.media.Schema(description = "MIME type", example = "application/pdf")
    private String fileType;
    @io.swagger.v3.oas.annotations.media.Schema(description = "Server storage path", example = "uploads/claims/medical_bill.pdf")
    private String filePath;

    @io.swagger.v3.oas.annotations.media.Schema(description = "Submission timestamp")
    private java.time.LocalDateTime uploadedAt;
}