package com.rms.notification.config.queue;

import com.rms.notification.utils.constants.QueueConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditRabbitMQConfig {

    @Bean
    public TopicExchange auditExchange() {
        return ExchangeBuilder
                .topicExchange(QueueConstants.Audit.EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public Queue auditQueue() {
        return QueueBuilder
                .durable(QueueConstants.Audit.Queue.MAIN)
                .withArgument("x-dead-letter-exchange", QueueConstants.Audit.EXCHANGE)
                .withArgument("x-dead-letter-routing-key", QueueConstants.Audit.RoutingKey.DLQ)
                .build();
    }

    @Bean
    public Queue auditDLQ() {
        return QueueBuilder
                .durable(QueueConstants.Audit.Queue.DLQ)
                .build();
    }

    @Bean
    public Binding auditBinding() {
        return BindingBuilder
                .bind(auditQueue())
                .to(auditExchange())
                .with(QueueConstants.Audit.RoutingKey.MAIN);
    }

    @Bean
    public Binding auditDLQBinding() {
        return BindingBuilder
                .bind(auditDLQ())
                .to(auditExchange())
                .with(QueueConstants.Audit.RoutingKey.DLQ);
    }
}
