package com.smartsure.admin.service;

import com.smartsure.admin.client.AuthServiceClient;
import com.smartsure.admin.client.ClaimsServiceClient;
import com.smartsure.admin.client.PolicyServiceClient;
import com.smartsure.admin.dto.*;
import com.smartsure.admin.entity.AdminAuditLog;
import com.smartsure.admin.exception.ResourceNotFoundException;
import com.smartsure.admin.exception.ServiceUnavailableException;
import com.smartsure.admin.repository.AdminAuditLogRepository;
import com.smartsure.admin.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;
import java.util.Objects;

@Service
public class AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    private final ClaimsServiceClient claimsServiceClient;
    private final PolicyServiceClient policyServiceClient;
    private final AuthServiceClient authServiceClient;
    private final AdminAuditLogRepository auditLogRepository;
    private final JwtUtil jwtUtil;

    public AdminService(ClaimsServiceClient claimsServiceClient,
                        PolicyServiceClient policyServiceClient,
                        AuthServiceClient authServiceClient,
                        AdminAuditLogRepository auditLogRepository,
                        JwtUtil jwtUtil) {
        this.claimsServiceClient = claimsServiceClient;
        this.policyServiceClient = policyServiceClient;
        this.authServiceClient = authServiceClient;
        this.auditLogRepository = auditLogRepository;
        this.jwtUtil = jwtUtil;
    }

    @CircuitBreaker(name = "claimsService", fallbackMethod = "reviewClaimFallback")
    @CacheEvict(value = {"pendingClaims", "dashboardReports"}, allEntries = true)
    public ClaimReviewResponse reviewClaim(Long claimId, ClaimReviewRequest request, String authorizationHeader) {
        try {
            logger.info("Reviewing claim id={} with status={}", claimId, request.getStatus());

            ClaimDetailsDTO updatedClaim = claimsServiceClient.updateClaimStatus(claimId, request.getStatus().name());
            if (updatedClaim == null) {
                throw new ResourceNotFoundException("Claim not found or could not be updated");
            }

            String token = authorizationHeader.substring(7);
            String adminEmail = jwtUtil.extractUsername(token);

            auditLogRepository.save(AdminAuditLog.builder()
                    .adminEmail(adminEmail)
                    .action("CLAIM_REVIEW")
                    .targetType("CLAIM")
                    .targetId(claimId)
                    .oldValue("UNDER_REVIEW")
                    .newValue(request.getStatus().name())
                    .remarks(request.getRemarks())
                    .build());

            return new ClaimReviewResponse(
                    claimId,
                    request.getStatus().name(),
                    "Claim reviewed successfully"
            );
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceUnavailableException("Claims service is unavailable");
        }
    }

    @CircuitBreaker(name = "multiService", fallbackMethod = "generateReportsFallback")
    @Cacheable(value = "dashboardReports", key = "'admin-reports'")
    public ReportDTO generateReports() {
        try {
            Long totalUsers = authServiceClient.getTotalUsers();
            Long totalPolicies = policyServiceClient.getTotalPolicies();
            Long totalClaims = claimsServiceClient.getTotalClaims();
            Long pendingClaims = claimsServiceClient.getPendingClaimsCount();

            return ReportDTO.builder()
                    .totalUsers(Objects.requireNonNullElse(totalUsers, 0L))
                    .totalPolicies(Objects.requireNonNullElse(totalPolicies, 0L))
                    .totalClaims(Objects.requireNonNullElse(totalClaims, 0L))
                    .pendingClaims(Objects.requireNonNullElse(pendingClaims, 0L))
                    .build();
        } catch (Exception ex) {
            throw new ServiceUnavailableException("Unable to generate report because one or more services are unavailable");
        }
    }

    public DashboardReportDTO getDashboardReport() {
        ReportDTO report = generateReports();
        return DashboardReportDTO.builder()
                .totalUsers(report.getTotalUsers())
                .totalPolicies(report.getTotalPolicies())
                .totalClaims(report.getTotalClaims())
                .pendingClaims(report.getPendingClaims())
                .status("Dashboard generated successfully")
                .build();
    }

    @CircuitBreaker(name = "authService", fallbackMethod = "getAllUsersFallback")
    public List<UserSummaryDTO> getAllUsers() {
        try {
            return authServiceClient.getAllUsers();
        } catch (Exception ex) {
            throw new ServiceUnavailableException("Auth service is unavailable");
        }
    }

    @CircuitBreaker(name = "authService", fallbackMethod = "getUserByIdFallback")
    public UserSummaryDTO getUserById(Long id) {
        try {
            UserSummaryDTO user = authServiceClient.getUserById(id);
            if (user == null) {
                throw new ResourceNotFoundException("User not found");
            }
            return user;
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceUnavailableException("Auth service is unavailable");
        }
    }

    @CircuitBreaker(name = "authService", fallbackMethod = "updateUserStatusFallback")
    @CacheEvict(value = "dashboardReports", allEntries = true)
    public UserSummaryDTO updateUserStatus(Long id, UserStatusUpdateRequest request, String authorizationHeader) {
        try {
            UserSummaryDTO updated = authServiceClient.updateUserStatus(id, request);
            if (updated == null) {
                throw new ResourceNotFoundException("User not found or status update failed");
            }

            String token = authorizationHeader.substring(7);
            String adminEmail = jwtUtil.extractUsername(token);

            auditLogRepository.save(AdminAuditLog.builder()
                    .adminEmail(adminEmail)
                    .action("USER_STATUS_UPDATE")
                    .targetType("USER")
                    .targetId(id)
                    .oldValue("UNKNOWN")
                    .newValue(request.getStatus())
                    .remarks("User status updated by admin")
                    .build());

            return updated;
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceUnavailableException("Auth service is unavailable");
        }
    }

    @CircuitBreaker(name = "claimsService", fallbackMethod = "getPendingClaimsFallback")
    @Cacheable(value = "pendingClaims", key = "'pending-claims'")
    public List<ClaimDetailsDTO> getPendingClaims() {
        try {
            return claimsServiceClient.getPendingClaims();
        } catch (Exception ex) {
            throw new ServiceUnavailableException("Claims service is unavailable");
        }
    }

    @CircuitBreaker(name = "claimsService", fallbackMethod = "getClaimByIdFallback")
    public ClaimDetailsDTO getClaimById(Long claimId) {
        try {
            ClaimDetailsDTO claim = claimsServiceClient.getClaimById(claimId);
            if (claim == null) {
                throw new ResourceNotFoundException("Claim not found");
            }
            return claim;
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceUnavailableException("Claims service is unavailable");
        }
    }

    @CircuitBreaker(name = "claimsService", fallbackMethod = "getClaimDocumentsFallback")
    public List<ClaimDocumentDTO> getClaimDocuments(Long claimId) {
        try {
            return claimsServiceClient.getClaimDocuments(claimId);
        } catch (Exception ex) {
            throw new ServiceUnavailableException("Claims service is unavailable");
        }
    }

    public ClaimReviewResponse reviewClaimFallback(Long claimId, ClaimReviewRequest request, String authorizationHeader, Throwable throwable) {
        throw new ServiceUnavailableException("Claims service is currently unavailable. Cannot review claim.");
    }
    public ReportDTO generateReportsFallback(Throwable throwable) {
        throw new ServiceUnavailableException("One or more services are currently unavailable. Cannot generate reports.");
    }
    public List<UserSummaryDTO> getAllUsersFallback(Throwable throwable) {
        throw new ServiceUnavailableException("Auth service is currently unavailable.");
    }
    public UserSummaryDTO getUserByIdFallback(Long id, Throwable throwable) {
        throw new ServiceUnavailableException("Auth service is currently unavailable.");
    }
    public UserSummaryDTO updateUserStatusFallback(Long id, UserStatusUpdateRequest request, String authorizationHeader, Throwable throwable) {
        throw new ServiceUnavailableException("Auth service is currently unavailable.");
    }
    public List<ClaimDetailsDTO> getPendingClaimsFallback(Throwable throwable) {
        throw new ServiceUnavailableException("Claims service is currently unavailable.");
    }
    public ClaimDetailsDTO getClaimByIdFallback(Long claimId, Throwable throwable) {
        throw new ServiceUnavailableException("Claims service is currently unavailable.");
    }
    public List<ClaimDocumentDTO> getClaimDocumentsFallback(Long claimId, Throwable throwable) {
        throw new ServiceUnavailableException("Claims service is currently unavailable.");
    }

    @CacheEvict(value = "dashboardReports", allEntries = true)
    @CircuitBreaker(name = "policyService", fallbackMethod = "createPolicyProductFallback")
    public PolicyTypeDTO createPolicyProduct(PolicyTypeDTO request) {
        return policyServiceClient.createPolicyType(request);
    }

    @CacheEvict(value = "dashboardReports", allEntries = true)
    @CircuitBreaker(name = "policyService", fallbackMethod = "updatePolicyProductFallback")
    public PolicyTypeDTO updatePolicyProduct(Long id, PolicyTypeDTO request) {
        return policyServiceClient.updatePolicyType(id, request);
    }

    @CacheEvict(value = "dashboardReports", allEntries = true)
    @CircuitBreaker(name = "policyService", fallbackMethod = "deletePolicyProductFallback")
    public void deletePolicyProduct(Long id) {
        policyServiceClient.deletePolicyType(id);
    }

    public PolicyTypeDTO createPolicyProductFallback(PolicyTypeDTO request, Throwable throwable) {
        throw new ServiceUnavailableException("Policy service is currently unavailable. Reason: " + throwable.getMessage());
    }

    public PolicyTypeDTO updatePolicyProductFallback(Long id, PolicyTypeDTO request, Throwable throwable) {
        throw new ServiceUnavailableException("Policy service is currently unavailable. Reason: " + throwable.getMessage());
    }

    public void deletePolicyProductFallback(Long id, Throwable throwable) {
        throw new ServiceUnavailableException("Policy service is currently unavailable. Reason: " + throwable.getMessage());
    }
}