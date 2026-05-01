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

import org.springframework.cache.annotation.CacheEvict;
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
    private final com.smartsure.claims.mapper.ClaimMapper claimMapper;

    public ClaimService(ClaimRepository claimRepository,
                        ClaimDocumentRepository documentRepository,
                        PolicyServiceClient policyServiceClient,
                        FileStorageUtil fileStorageUtil,
                        ClaimEventPublisher claimEventPublisher,
                        com.smartsure.claims.mapper.ClaimMapper claimMapper) {
        this.claimRepository = claimRepository;
        this.documentRepository = documentRepository;
        this.policyServiceClient = policyServiceClient;
        this.fileStorageUtil = fileStorageUtil;
        this.claimEventPublisher = claimEventPublisher;
        this.claimMapper = claimMapper;
    }

    @CircuitBreaker(name = "policyService", fallbackMethod = "initiateClaimFallback")
    @CacheEvict(value = {"userClaims", "pendingClaims"}, allEntries = true)
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

        return claimMapper.toClaimDocumentDTO(document);
    }

    public ClaimDTO getClaimStatus(Long claimId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found"));
        return mapToDTO(claim);
    }

    @org.springframework.cache.annotation.Cacheable(value = "userClaims", key = "#userId")
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

    @org.springframework.cache.annotation.Cacheable(value = "pendingClaims", key = "'pending'")
    public List<ClaimDTO> getPendingClaims() {
        return claimRepository.findByStatusIn(List.of(ClaimStatus.SUBMITTED, ClaimStatus.UNDER_REVIEW))
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public List<ClaimDocumentDTO> getClaimDocuments(Long claimId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found"));
        return claimMapper.toClaimDocumentDTOList(documentRepository.findByClaim_Id(claim.getId()));
    }

    private ClaimDTO mapToDTO(Claim claim) {
        List<ClaimDocument> documents = documentRepository.findByClaim_Id(claim.getId());
        return claimMapper.toClaimDTO(claim, documents);
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