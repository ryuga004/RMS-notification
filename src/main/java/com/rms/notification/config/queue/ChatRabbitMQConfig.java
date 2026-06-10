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
public class ChatRabbitMQConfig {

    @Bean
    public TopicExchange chatExchange() {
        return ExchangeBuilder
                .topicExchange(QueueConstants.Chat.EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public Queue chatQueue() {
        return QueueBuilder
                .durable(QueueConstants.Chat.Queue.MAIN)
                .withArgument("x-dead-letter-exchange", QueueConstants.Chat.EXCHANGE)
                .withArgument("x-dead-letter-routing-key", QueueConstants.Chat.RoutingKey.DLQ)
                .build();
    }

    @Bean
    public Queue chatDLQ() {
        return QueueBuilder
                .durable(QueueConstants.Chat.Queue.DLQ)
                .build();
    }

    @Bean
    public Binding chatBinding() {
        return BindingBuilder
                .bind(chatQueue())
                .to(chatExchange())
                .with(QueueConstants.Chat.RoutingKey.MAIN);
    }

    @Bean
    public Binding chatDLQBinding() {
        return BindingBuilder
                .bind(chatDLQ())
                .to(chatExchange())
                .with(QueueConstants.Chat.RoutingKey.DLQ);
    }
}
