package com.rms.notification.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
@Slf4j
public class RabbitMqConfig {

    @Value("${mq.host:localhost}")
    private String host;

    @Value("${mq.port:5672}")
    private int port;

    @Value("${mq.username:guest}")
    private String username;

    @Value("${mq.password:guest}")
    private String password;

    @Value("${mq.ssl:false}")
    private boolean useSsl;

    @Value("${mq.virtual-host:/}")
    private String virtualHost;

    @Bean
    public ConnectionFactory connectionFactory() throws Exception {
        log.info("Initializing RabbitMQ connection factory");
        RabbitConnectionFactoryBean factoryBean = getRabbitConnectionFactoryBean();
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(factoryBean.getObject());
        cachingConnectionFactory.setConnectionNameStrategy(
                cf -> "NotificationService-" + UUID.randomUUID()
        );
        cachingConnectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CHANNEL);
        cachingConnectionFactory.setChannelCheckoutTimeout(5000);
        return cachingConnectionFactory;
    }

    private RabbitConnectionFactoryBean getRabbitConnectionFactoryBean() {
        RabbitConnectionFactoryBean factoryBean = new RabbitConnectionFactoryBean();
        factoryBean.setHost(host);
        factoryBean.setPort(port);
        factoryBean.setUsername(username);
        factoryBean.setPassword(password);
        factoryBean.setVirtualHost(virtualHost);
        factoryBean.setUseSSL(useSsl);
        factoryBean.setConnectionTimeout(10_000);
        factoryBean.setRequestedHeartbeat(10);
        factoryBean.afterPropertiesSet();
        return factoryBean;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        factory.setPrefetchCount(1);
        factory.setDefaultRequeueRejected(false);
        factory.setAdviceChain(
                RetryInterceptorBuilder.stateless()
                        .maxAttempts(3)
                        .backOffOptions(5000, 2.0, 30000)
                        .recoverer(new RejectAndDontRequeueRecoverer())
                        .build()
        );
        return factory;
    }
}
