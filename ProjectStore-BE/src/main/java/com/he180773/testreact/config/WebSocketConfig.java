package com.he180773.testreact.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setHandshakeHandler(new CustomHandshakeHandler())
                .setAllowedOriginPatterns("*")
                .withSockJS(); // frontend kết nối
    }
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue"); // tin nhắn trả lại client
        registry.setApplicationDestinationPrefixes("/app"); // tiền tố khi client gửi vào
        registry.setUserDestinationPrefix("/user"); // để gửi riêng theo user
    }
}

