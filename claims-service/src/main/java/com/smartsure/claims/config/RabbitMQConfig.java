package com.smartsure.claims.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE = "smartsure.claims.exchange";
    public static final String QUEUE_CLAIM_SUBMITTED = "claim.submitted.queue";
    public static final String QUEUE_CLAIM_STATUS_CHANGED = "claim.status.changed.queue";
    public static final String ROUTING_KEY_CLAIM_SUBMITTED = "claim.submitted.routingKey";
    public static final String ROUTING_KEY_CLAIM_STATUS_CHANGED = "claim.status.changed.routingKey";

    @Bean
    public TopicExchange claimsExchange() {
        return new TopicExchange(EXCHANGE);
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

    @Bean
    public MessageConverter converter(){
        return new Jackson2JsonMessageConverter();
    }
}
