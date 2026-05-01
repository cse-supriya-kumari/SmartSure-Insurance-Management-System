package com.smartsure.policy.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE = "smartsure.policy.exchange";
    public static final String QUEUE_POLICY_PURCHASED = "policy.purchased.queue";
    public static final String ROUTING_KEY_POLICY_PURCHASED = "policy.purchased.routingKey";

    @Bean
    public TopicExchange policyExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue policyPurchasedQueue() {
        return new Queue(QUEUE_POLICY_PURCHASED);
    }

    @Bean
    public Binding policyPurchasedBinding(Queue policyPurchasedQueue, TopicExchange policyExchange) {
        return BindingBuilder.bind(policyPurchasedQueue).to(policyExchange).with(ROUTING_KEY_POLICY_PURCHASED);
    }

    @Bean
    public MessageConverter converter(){
        return new Jackson2JsonMessageConverter();
    }
}
