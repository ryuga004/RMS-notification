package com.rms.notification.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${stomp.relay.host}")
    private String relayHost;

    @Value("${stomp.relay.port}")
    private int relayPort;

    @Value("${stomp.relay.username}")
    private String relayUsername;

    @Value("${stomp.relay.password}")
    private String relayPassword;

    @Value("${stomp.relay.system-login}")
    private String relaySystemLogin;

    @Value("${stomp.relay.system-passcode}")
    private String relaySystemPasscode;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
        registry.enableSimpleBroker("/topic", "/queue", "/exchange");
        // registry.enableStompBrokerRelay("/topic", "/queue", "/exchange")
        //         .setRelayHost(relayHost)
        //         .setRelayPort(relayPort)
        //         .setClientLogin(relayUsername)
        //         .setClientPasscode(relayPassword)
        //         .setSystemLogin(relaySystemLogin)
        //         .setSystemPasscode(relaySystemPasscode);
    }
}
