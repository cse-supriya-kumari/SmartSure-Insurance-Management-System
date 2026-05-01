package com.smartsure.claims.repository;

import com.smartsure.claims.entity.Claim;
import com.smartsure.claims.entity.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByUserId(Long userId);
    List<Claim> findByPolicyId(Long policyId);
    long countByStatusIn(List<ClaimStatus> statuses);
    List<Claim> findByStatusIn(List<ClaimStatus> statuses);
}