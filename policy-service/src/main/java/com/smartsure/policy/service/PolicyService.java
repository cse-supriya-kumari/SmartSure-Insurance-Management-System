package com.smartsure.policy.service;

import com.smartsure.policy.client.AuthServiceClient;
import com.smartsure.policy.dto.PolicyDTO;
import com.smartsure.policy.dto.PolicyPurchaseRequest;
import com.smartsure.policy.dto.PolicyTypeDTO;
import com.smartsure.policy.dto.PremiumDTO;
import com.smartsure.policy.dto.UserSummaryDTO;
import com.smartsure.policy.entity.*;
import com.smartsure.policy.exception.InvalidOperationException;
import com.smartsure.policy.exception.ResourceNotFoundException;
import com.smartsure.policy.exception.ServiceUnavailableException;
import com.smartsure.policy.exception.UnauthorizedException;
import feign.FeignException;
import com.smartsure.policy.repository.PolicyRepository;
import com.smartsure.policy.repository.PolicyTypeRepository;
import com.smartsure.policy.repository.PremiumRepository;
import com.smartsure.policy.event.PolicyEventPublisher;
import com.smartsure.policy.event.PolicyPurchasedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final PolicyTypeRepository policyTypeRepository;
    private final PremiumRepository premiumRepository;
    private final AuthServiceClient authServiceClient;
    private final PolicyEventPublisher policyEventPublisher;

    public PolicyService(PolicyRepository policyRepository,
                         PolicyTypeRepository policyTypeRepository,
                         PremiumRepository premiumRepository,
                         AuthServiceClient authServiceClient,
                         PolicyEventPublisher policyEventPublisher) {
        this.policyRepository = policyRepository;
        this.policyTypeRepository = policyTypeRepository;
        this.premiumRepository = premiumRepository;
        this.authServiceClient = authServiceClient;
        this.policyEventPublisher = policyEventPublisher;
    }

    @Transactional
    @CircuitBreaker(name = "authService", fallbackMethod = "purchasePolicyFallback")
    public PolicyDTO purchasePolicy(PolicyPurchaseRequest request, String token) {
        UserSummaryDTO user = authServiceClient.validateToken(token);
        if (user == null || (!"CUSTOMER".equals(user.getRole()) && !"ADMIN".equals(user.getRole()))) {
            throw new UnauthorizedException("Only customers can purchase policies");
        }

        PolicyType type = policyTypeRepository.findById(request.getPolicyTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Policy Type not found"));

        Policy policy = Policy.builder()
                .userId(request.getUserId())
                .policyType(type)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(type.getDurationMonths()))
                .premiumAmount(type.getBasePremium())
                .status(PolicyStatus.ACTIVE)
                .build();

        policy = policyRepository.save(policy);

        List<Premium> premiums = new ArrayList<>();
        BigDecimal monthlyInstallment = type.getBasePremium()
                .divide(new BigDecimal(type.getDurationMonths()), 2, RoundingMode.HALF_UP);

        for (int i = 0; i < type.getDurationMonths(); i++) {
            Premium premium = Premium.builder()
                    .policy(policy)
                    .amount(monthlyInstallment)
                    .dueDate(policy.getStartDate().plusMonths(i + 1))
                    .status(PremiumStatus.PENDING)
                    .build();
            premiums.add(premium);
        }

        premiums = premiumRepository.saveAll(premiums);

        PolicyPurchasedEvent event = PolicyPurchasedEvent.builder()
                .policyId(policy.getId())
                .userId(policy.getUserId())
                .policyTypeName(type.getName())
                .premiumAmount(policy.getPremiumAmount())
                .timestamp(java.time.LocalDateTime.now())
                .build();
        policyEventPublisher.publishPolicyPurchasedEvent(event);

        return mapToDTO(policy, premiums);
    }

    public List<PolicyDTO> getUserPolicies(Long userId) {
        return policyRepository.findByUserId(userId)
                .stream()
                .map(this::mapToDTOWithPremiums)
                .toList();
    }

    public PolicyDTO getPolicyById(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));
        return mapToDTOWithPremiums(policy);
    }

    public List<PolicyTypeDTO> getAllPolicyTypes() {
        return policyTypeRepository.findAll()
                .stream()
                .map(type -> PolicyTypeDTO.builder()
                        .id(type.getId())
                        .name(type.getName())
                        .description(type.getDescription())
                        .basePremium(type.getBasePremium())
                        .coverageAmount(type.getCoverageAmount())
                        .durationMonths(type.getDurationMonths())
                        .build())
                .toList();
    }

    @Transactional
    public PolicyTypeDTO createPolicyType(PolicyTypeDTO request, String token) {
        UserSummaryDTO user = authServiceClient.validateToken(token);
        if (user == null || !"ADMIN".equals(user.getRole())) {
            throw new InvalidOperationException("Only admins can create policy types");
        }
        PolicyType type = PolicyType.builder()
                .name(request.getName())
                .description(request.getDescription())
                .basePremium(request.getBasePremium())
                .coverageAmount(request.getCoverageAmount())
                .durationMonths(request.getDurationMonths())
                .build();
        type = policyTypeRepository.save(type);
        request.setId(type.getId());
        return request;
    }

    @Transactional
    public PolicyTypeDTO updatePolicyType(Long id, PolicyTypeDTO request, String token) {
        UserSummaryDTO user = authServiceClient.validateToken(token);
        if (user == null || !"ADMIN".equals(user.getRole())) {
            throw new InvalidOperationException("Only admins can update policy types");
        }
        PolicyType type = policyTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy Type not found"));
        type.setName(request.getName());
        type.setDescription(request.getDescription());
        type.setBasePremium(request.getBasePremium());
        type.setCoverageAmount(request.getCoverageAmount());
        type.setDurationMonths(request.getDurationMonths());
        policyTypeRepository.save(type);
        request.setId(type.getId());
        return request;
    }

    @Transactional
    public void deletePolicyType(Long id, String token) {
        UserSummaryDTO user = authServiceClient.validateToken(token);
        if (user == null || !"ADMIN".equals(user.getRole())) {
            throw new InvalidOperationException("Only admins can delete policy types");
        }
        if (!policyTypeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Policy Type not found");
        }
        policyTypeRepository.deleteById(id);
    }

    public long getTotalPolicies() {
        return policyRepository.count();
    }

    private PolicyDTO mapToDTOWithPremiums(Policy policy) {
        List<Premium> premiums = premiumRepository.findByPolicyId(policy.getId());
        return mapToDTO(policy, premiums);
    }

    private PolicyDTO mapToDTO(Policy policy, List<Premium> premiums) {
        List<PremiumDTO> premiumDTOs = premiums.stream()
                .map(p -> PremiumDTO.builder()
                        .id(p.getId())
                        .amount(p.getAmount())
                        .dueDate(p.getDueDate())
                        .paidDate(p.getPaidDate())
                        .status(p.getStatus().name())
                        .build())
                .toList();

        return PolicyDTO.builder()
                .id(policy.getId())
                .userId(policy.getUserId())
                .policyTypeId(policy.getPolicyType().getId())
                .policyTypeName(policy.getPolicyType().getName())
                .startDate(policy.getStartDate())
                .endDate(policy.getEndDate())
                .premiumAmount(policy.getPremiumAmount())
                .status(policy.getStatus().name())
                .premiums(premiumDTOs)
                .build();
    }

    public PolicyDTO purchasePolicyFallback(PolicyPurchaseRequest request, String token, Throwable throwable) {
        if (throwable instanceof FeignException feignException) {
            if (feignException.status() == 401 || feignException.status() == 403) {
                throw new UnauthorizedException("Authentication failed: Invalid or expired token.");
            }
            throw new ServiceUnavailableException("Auth service is currently unavailable. Details: " + throwable.getMessage());
        }
        if (throwable.getMessage() != null && throwable.getMessage().contains("Connection refused")) {
             throw new ServiceUnavailableException("A required backend service (like RabbitMQ or Database) is unavailable. Please ensure RabbitMQ is running. Details: " + throwable.getMessage());
        }
        throw new ServiceUnavailableException("Operation failed: " + throwable.getMessage());
    }
}