package com.smartsure.policy.config;

import com.smartsure.policy.entity.PolicyType;
import com.smartsure.policy.repository.PolicyTypeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataSeeder implements CommandLineRunner {

    private final PolicyTypeRepository policyTypeRepository;

    public DataSeeder(PolicyTypeRepository policyTypeRepository) {
        this.policyTypeRepository = policyTypeRepository;
    }

    @Override
    public void run(String... args) {
        if (policyTypeRepository.count() == 0) {
            policyTypeRepository.save(PolicyType.builder()
                    .name("Health Insurance")
                    .description("Covers hospitalization and medical expenses")
                    .basePremium(new BigDecimal("12000"))
                    .coverageAmount(new BigDecimal("300000"))
                    .durationMonths(12)
                    .build());

            policyTypeRepository.save(PolicyType.builder()
                    .name("Life Insurance")
                    .description("Provides life coverage and financial security")
                    .basePremium(new BigDecimal("18000"))
                    .coverageAmount(new BigDecimal("500000"))
                    .durationMonths(12)
                    .build());

            policyTypeRepository.save(PolicyType.builder()
                    .name("Vehicle Insurance")
                    .description("Covers accidental damage and third-party liability")
                    .basePremium(new BigDecimal("15000"))
                    .coverageAmount(new BigDecimal("250000"))
                    .durationMonths(12)
                    .build());

            policyTypeRepository.save(PolicyType.builder()
                    .name("Travel Insurance")
                    .description("Covers trip cancellation, baggage loss, and emergencies")
                    .basePremium(new BigDecimal("8000"))
                    .coverageAmount(new BigDecimal("150000"))
                    .durationMonths(6)
                    .build());
        }
    }
}