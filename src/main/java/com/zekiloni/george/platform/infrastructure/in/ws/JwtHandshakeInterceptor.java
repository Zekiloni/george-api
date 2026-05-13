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

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    public static final String AUTHENTICATION_ATTR = "authentication";

    private final JwtDecoder jwtDecoder;
    private final KeycloakRoleConverter roleConverter = new KeycloakRoleConverter();

    private static final String PROTOCOL_PREFIX = "bearer.";

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String token = extractToken(request, response);
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

    /**
     * Reads the operator's JWT from {@code Sec-WebSocket-Protocol: bearer.<jwt>}.
     * Falls back to the legacy {@code ?token=} URL form for transitional
     * compatibility; bearer-in-URL is a real leak through logs/Referer.
     */
    private String extractToken(ServerHttpRequest request, ServerHttpResponse response) {
        List<String> offered = request.getHeaders().get("Sec-WebSocket-Protocol");
        if (offered != null) {
            for (String headerValue : offered) {
                for (String proto : headerValue.split(",")) {
                    String trimmed = proto.trim();
                    if (trimmed.startsWith(PROTOCOL_PREFIX) && trimmed.length() > PROTOCOL_PREFIX.length()) {
                        // Echo back so the browser accepts the upgrade.
                        response.getHeaders().set("Sec-WebSocket-Protocol", trimmed);
                        return trimmed.substring(PROTOCOL_PREFIX.length());
                    }
                }
            }
        }
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String legacy = servletRequest.getServletRequest().getParameter("token");
            if (legacy != null && !legacy.isBlank()) {
                log.warn("Operator WS used legacy URL token — client should send Sec-WebSocket-Protocol: bearer.<jwt>");
                return legacy;
            }
        }
        return null;
    }
}
