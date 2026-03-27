package com.smartsure.policy.event;

import com.smartsure.policy.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PolicyEventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(PolicyEventPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    public PolicyEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishPolicyPurchasedEvent(PolicyPurchasedEvent event) {
        logger.info("Publishing PolicyPurchasedEvent: {}", event);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY_POLICY_PURCHASED, event);
    }
}
