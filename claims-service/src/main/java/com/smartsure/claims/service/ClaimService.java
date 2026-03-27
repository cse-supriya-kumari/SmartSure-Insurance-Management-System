package com.smartsure.claims.service;

import com.smartsure.claims.client.PolicyServiceClient;
import com.smartsure.claims.dto.*;
import com.smartsure.claims.entity.*;
import com.smartsure.claims.exception.InvalidOperationException;
import com.smartsure.claims.exception.ResourceNotFoundException;
import com.smartsure.claims.repository.ClaimDocumentRepository;
import com.smartsure.claims.repository.ClaimRepository;
import com.smartsure.claims.util.FileStorageUtil;
import com.smartsure.claims.event.ClaimEventPublisher;
import com.smartsure.claims.event.ClaimSubmittedEvent;
import com.smartsure.claims.event.ClaimStatusChangedEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.io.IOException;
import java.util.List;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final ClaimDocumentRepository documentRepository;
    private final PolicyServiceClient policyServiceClient;
    private final FileStorageUtil fileStorageUtil;
    private final ClaimEventPublisher claimEventPublisher;

    public ClaimService(ClaimRepository claimRepository,
                        ClaimDocumentRepository documentRepository,
                        PolicyServiceClient policyServiceClient,
                        FileStorageUtil fileStorageUtil,
                        ClaimEventPublisher claimEventPublisher) {
        this.claimRepository = claimRepository;
        this.documentRepository = documentRepository;
        this.policyServiceClient = policyServiceClient;
        this.fileStorageUtil = fileStorageUtil;
        this.claimEventPublisher = claimEventPublisher;
    }

    @CircuitBreaker(name = "policyService", fallbackMethod = "initiateClaimFallback")
    public ClaimDTO initiateClaim(ClaimInitiateRequest request) {
        try {
            policyServiceClient.getPolicyById(request.getPolicyId());
        } catch (Exception ex) {
            throw new InvalidOperationException("Invalid Policy ID");
        }

        Claim claim = Claim.builder()
                .policyId(request.getPolicyId())
                .userId(request.getUserId())
                .description(request.getDescription())
                .status(ClaimStatus.SUBMITTED)
                .build();

        claim = claimRepository.save(claim);

        ClaimSubmittedEvent event = ClaimSubmittedEvent.builder()
                .claimId(claim.getId())
                .policyId(claim.getPolicyId())
                .userId(claim.getUserId())
                .status(claim.getStatus().name())
                .timestamp(java.time.LocalDateTime.now())
                .build();
        claimEventPublisher.publishClaimSubmittedEvent(event);

        return mapToDTO(claim);
    }

    public ClaimDocumentDTO uploadDocument(Long claimId, MultipartFile file) throws IOException {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found"));

        String savedPath = fileStorageUtil.store(file);

        ClaimDocument document = ClaimDocument.builder()
                .claim(claim)
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .filePath(savedPath)
                .build();

        document = documentRepository.save(document);

        if (claim.getStatus() == ClaimStatus.SUBMITTED) {
            String oldStatus = claim.getStatus().name();
            claim.setStatus(ClaimStatus.UNDER_REVIEW);
            claimRepository.save(claim);

            ClaimStatusChangedEvent event = ClaimStatusChangedEvent.builder()
                    .claimId(claim.getId())
                    .oldStatus(oldStatus)
                    .newStatus(ClaimStatus.UNDER_REVIEW.name())
                    .timestamp(java.time.LocalDateTime.now())
                    .build();
            claimEventPublisher.publishClaimStatusChangedEvent(event);
        }

        return ClaimDocumentDTO.builder()
                .id(document.getId())
                .fileName(document.getFileName())
                .fileType(document.getFileType())
                .filePath(document.getFilePath())
                .build();
    }

    public ClaimDTO getClaimStatus(Long claimId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found"));
        return mapToDTO(claim);
    }

    public List<ClaimDTO> getUserClaims(Long userId) {
        return claimRepository.findByUserId(userId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public ClaimDTO updateClaimStatus(Long claimId, String status) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found"));

        ClaimStatus parsedStatus;
        try {
            parsedStatus = ClaimStatus.valueOf(status.toUpperCase());
        } catch (Exception ex) {
            throw new InvalidOperationException("Invalid claim status");
        }

        String oldStatus = claim.getStatus().name();
        claim.setStatus(parsedStatus);
        claim = claimRepository.save(claim);

        ClaimStatusChangedEvent event = ClaimStatusChangedEvent.builder()
                .claimId(claim.getId())
                .oldStatus(oldStatus)
                .newStatus(parsedStatus.name())
                .timestamp(java.time.LocalDateTime.now())
                .build();
        claimEventPublisher.publishClaimStatusChangedEvent(event);

        return mapToDTO(claim);
    }

    public long getTotalClaims() {
        return claimRepository.count();
    }

    public long getPendingClaimsCount() {
        return claimRepository.countByStatusIn(List.of(ClaimStatus.SUBMITTED, ClaimStatus.UNDER_REVIEW));
    }

    public List<ClaimDTO> getPendingClaims() {
        return claimRepository.findByStatusIn(List.of(ClaimStatus.SUBMITTED, ClaimStatus.UNDER_REVIEW))
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public List<ClaimDocumentDTO> getClaimDocuments(Long claimId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found"));
        return documentRepository.findByClaim_Id(claim.getId())
                .stream()
                .map(d -> ClaimDocumentDTO.builder()
                        .id(d.getId())
                        .fileName(d.getFileName())
                        .fileType(d.getFileType())
                        .filePath(d.getFilePath())
                        .build())
                .toList();
    }

    private ClaimDTO mapToDTO(Claim claim) {
        List<ClaimDocumentDTO> documentDTOs = documentRepository.findByClaim_Id(claim.getId())
                .stream()
                .map(d -> ClaimDocumentDTO.builder()
                        .id(d.getId())
                        .fileName(d.getFileName())
                        .fileType(d.getFileType())
                        .filePath(d.getFilePath())
                        .build())
                .toList();

        return ClaimDTO.builder()
                .id(claim.getId())
                .policyId(claim.getPolicyId())
                .userId(claim.getUserId())
                .description(claim.getDescription())
                .status(claim.getStatus().name())
                .documents(documentDTOs)
                .build();
    }

    public ClaimDTO initiateClaimFallback(ClaimInitiateRequest request, Throwable throwable) {
        if (throwable instanceof InvalidOperationException) {
             throw (InvalidOperationException) throwable;
        }
        if (throwable.getMessage() != null && throwable.getMessage().contains("Connection refused")) {
             throw new InvalidOperationException("A required backend service (like RabbitMQ) is down. Details: " + throwable.getMessage());
        }
        throw new InvalidOperationException("Operation failed: " + throwable.getMessage());
    }
}