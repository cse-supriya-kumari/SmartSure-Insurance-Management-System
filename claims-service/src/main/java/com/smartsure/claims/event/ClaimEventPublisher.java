package com.smartsure.claims.event;

import com.smartsure.claims.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class ClaimEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(ClaimEventPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    public ClaimEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishClaimSubmittedEvent(ClaimSubmittedEvent event) {
        logger.info("Publishing ClaimSubmittedEvent: {}", event);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY_CLAIM_SUBMITTED, event);
    }

    public void publishClaimStatusChangedEvent(ClaimStatusChangedEvent event) {
        logger.info("Publishing ClaimStatusChangedEvent: {}", event);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY_CLAIM_STATUS_CHANGED, event);
    }
}
