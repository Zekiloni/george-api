package com.zekiloni.george.platform.infrastructure.in.ws;

import com.zekiloni.george.common.infrastructure.out.integration.keycloak.KeycloakRoleConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    public static final String AUTHENTICATION_ATTR = "authentication";

    private final JwtDecoder jwtDecoder;
    private final KeycloakRoleConverter roleConverter = new KeycloakRoleConverter();

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String token = extractToken(request);
        if (token == null) {
            log.debug("WS handshake rejected: missing token");
            return false;
        }

        try {
            Jwt jwt = jwtDecoder.decode(token);
            AbstractAuthenticationToken authentication = new JwtAuthenticationToken(jwt, roleConverter.convert(jwt));
            authentication.setAuthenticated(true);
            attributes.put(AUTHENTICATION_ATTR, authentication);
            return true;
        } catch (JwtException e) {
            log.debug("WS handshake rejected: invalid token: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }

    private String extractToken(ServerHttpRequest request) {
        if (!(request instanceof ServletServerHttpRequest servletRequest)) return null;
        return servletRequest.getServletRequest().getParameter("token");
    }
}
