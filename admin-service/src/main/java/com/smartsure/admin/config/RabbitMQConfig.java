package com.smartsure.admin.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Policy exchange and queue (declared by policy-service, also declared here so admin can start independently)
    public static final String POLICY_EXCHANGE = "smartsure.policy.exchange";
    public static final String QUEUE_POLICY_PURCHASED = "policy.purchased.queue";
    public static final String ROUTING_KEY_POLICY_PURCHASED = "policy.purchased.routingKey";

    // Claims exchange and queues (declared by claims-service, also declared here so admin can start independently)
    public static final String CLAIMS_EXCHANGE = "smartsure.claims.exchange";
    public static final String QUEUE_CLAIM_SUBMITTED = "claim.submitted.queue";
    public static final String QUEUE_CLAIM_STATUS_CHANGED = "claim.status.changed.queue";
    public static final String ROUTING_KEY_CLAIM_SUBMITTED = "claim.submitted.routingKey";
    public static final String ROUTING_KEY_CLAIM_STATUS_CHANGED = "claim.status.changed.routingKey";

    // --- Policy exchange/queue/binding ---

    @Bean
    public TopicExchange policyExchange() {
        return new TopicExchange(POLICY_EXCHANGE);
    }

    @Bean
    public Queue policyPurchasedQueue() {
        return new Queue(QUEUE_POLICY_PURCHASED);
    }

    @Bean
    public Binding policyPurchasedBinding(Queue policyPurchasedQueue, TopicExchange policyExchange) {
        return BindingBuilder.bind(policyPurchasedQueue).to(policyExchange).with(ROUTING_KEY_POLICY_PURCHASED);
    }

    // --- Claims exchange/queues/bindings ---

    @Bean
    public TopicExchange claimsExchange() {
        return new TopicExchange(CLAIMS_EXCHANGE);
    }

    @Bean
    public Queue claimSubmittedQueue() {
        return new Queue(QUEUE_CLAIM_SUBMITTED);
    }

    @Bean
    public Binding claimSubmittedBinding(Queue claimSubmittedQueue, TopicExchange claimsExchange) {
        return BindingBuilder.bind(claimSubmittedQueue).to(claimsExchange).with(ROUTING_KEY_CLAIM_SUBMITTED);
    }

    @Bean
    public Queue claimStatusChangedQueue() {
        return new Queue(QUEUE_CLAIM_STATUS_CHANGED);
    }

    @Bean
    public Binding claimStatusChangedBinding(Queue claimStatusChangedQueue, TopicExchange claimsExchange) {
        return BindingBuilder.bind(claimStatusChangedQueue).to(claimsExchange).with(ROUTING_KEY_CLAIM_STATUS_CHANGED);
    }

    // --- Message converter ---

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }
}
