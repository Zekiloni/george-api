package com.zekiloni.george.platform.infrastructure.in.ws;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final VisitorWebSocketHandler visitorHandler;
    private final OperatorWebSocketHandler operatorHandler;
    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    @Value("${api.cors.allowed-origins}")
    private String allowedOrigins;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(visitorHandler, "/ws/visitor")
                .setAllowedOrigins(allowedOrigins);
        registry.addHandler(operatorHandler, "/ws/operator")
                .addInterceptors(jwtHandshakeInterceptor)
                .setAllowedOrigins(allowedOrigins);
    }
}
