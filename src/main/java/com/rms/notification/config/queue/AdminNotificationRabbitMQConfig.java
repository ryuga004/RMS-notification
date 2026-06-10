package com.rms.notification.config.queue;

import com.rms.notification.utils.constants.QueueConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminNotificationRabbitMQConfig {

    @Bean
    public TopicExchange adminNotificationExchange() {
        return ExchangeBuilder
                .topicExchange(QueueConstants.AdminNotification.EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public Queue adminNotificationQueue() {
        return QueueBuilder
                .durable(QueueConstants.AdminNotification.Queue.MAIN)
                .withArgument("x-dead-letter-exchange", QueueConstants.AdminNotification.EXCHANGE)
                .withArgument("x-dead-letter-routing-key", QueueConstants.AdminNotification.RoutingKey.DLQ)
                .build();
    }

    @Bean
    public Queue adminNotificationDLQ() {
        return QueueBuilder
                .durable(QueueConstants.AdminNotification.Queue.DLQ)
                .build();
    }

    @Bean
    public Binding adminNotificationBinding() {
        return BindingBuilder
                .bind(adminNotificationQueue())
                .to(adminNotificationExchange())
                .with(QueueConstants.AdminNotification.RoutingKey.MAIN);
    }

    @Bean
    public Binding adminNotificationDLQBinding() {
        return BindingBuilder
                .bind(adminNotificationDLQ())
                .to(adminNotificationExchange())
                .with(QueueConstants.AdminNotification.RoutingKey.DLQ);
    }
}
