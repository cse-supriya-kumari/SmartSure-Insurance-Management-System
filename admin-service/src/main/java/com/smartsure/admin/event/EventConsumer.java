package com.smartsure.admin.event;

import com.smartsure.admin.entity.AdminAuditLog;
import com.smartsure.admin.repository.AdminAuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private final AdminAuditLogRepository auditLogRepository;

    public EventConsumer(AdminAuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @RabbitListener(queues = "policy.purchased.queue")
    public void consumePolicyPurchasedEvent(Map<String, Object> event) {
        logger.info("Consumed PolicyPurchasedEvent: {}", event);
        auditLogRepository.save(AdminAuditLog.builder()
                .adminEmail("system@smartsure.com")
                .action("POLICY_PURCHASED")
                .targetType("POLICY")
                .targetId(((Number) event.get("policyId")).longValue())
                .oldValue(null)
                .newValue("ACTIVE")
                .remarks("Policy " + event.get("policyTypeName") + " purchased for amount: " + event.get("premiumAmount"))
                .build());
    }

    @RabbitListener(queues = "claim.submitted.queue")
    public void consumeClaimSubmittedEvent(Map<String, Object> event) {
        logger.info("Consumed ClaimSubmittedEvent: {}", event);
        auditLogRepository.save(AdminAuditLog.builder()
                .adminEmail("system@smartsure.com")
                .action("CLAIM_SUBMITTED")
                .targetType("CLAIM")
                .targetId(((Number) event.get("claimId")).longValue())
                .oldValue(null)
                .newValue((String) event.get("status"))
                .remarks("Claim submitted for policy: " + event.get("policyId"))
                .build());
    }

    @RabbitListener(queues = "claim.status.changed.queue")
    public void consumeClaimStatusChangedEvent(Map<String, Object> event) {
        logger.info("Consumed ClaimStatusChangedEvent: {}", event);
        auditLogRepository.save(AdminAuditLog.builder()
                .adminEmail("system@smartsure.com")
                .action("CLAIM_STATUS_CHANGED")
                .targetType("CLAIM")
                .targetId(((Number) event.get("claimId")).longValue())
                .oldValue((String) event.get("oldStatus"))
                .newValue((String) event.get("newStatus"))
                .remarks("Claim status changed automatically")
                .build());
    }
}
