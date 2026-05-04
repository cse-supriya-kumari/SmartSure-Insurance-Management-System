package com.smartsure.policy.repository;

import com.smartsure.policy.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {
    List<Policy> findByUserId(Long userId);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(p.premiumAmount) FROM Policy p")
    java.math.BigDecimal sumTotalPremiumAmount();
}