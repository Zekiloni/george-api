package com.zekiloni.george.platform.infrastructure.in.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

/**
 * Reads the visitor wsToken from the {@code Sec-WebSocket-Protocol} handshake
 * header instead of the URL query string. Browsers leak query params through
 * Referer, server access logs, and history; the subprotocol header doesn't
 * cross any of those boundaries.
 * <p>
 * Client sends:    {@code Sec-WebSocket-Protocol: visitor.<token>}<br>
 * Server echoes:   {@code Sec-WebSocket-Protocol: visitor.<token>}<br>
 * Attribute set:   {@code wsToken -> <token>}
 * <p>
 * Legacy {@code ?wsToken=…} URL form is still accepted with a deprecation
 * warning so existing visitor pages don't break mid-rollout. Remove the
 * fallback once every client is on the new path.
 */
@Component
@Slf4j
public class VisitorTokenHandshakeInterceptor implements HandshakeInterceptor {

    public static final String WS_TOKEN_ATTR = "wsToken";
    private static final String PROTOCOL_PREFIX = "visitor.";

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        List<String> offered = request.getHeaders().get("Sec-WebSocket-Protocol");
        if (offered != null) {
            for (String headerValue : offered) {
                for (String proto : headerValue.split(",")) {
                    String trimmed = proto.trim();
                    if (trimmed.startsWith(PROTOCOL_PREFIX) && trimmed.length() > PROTOCOL_PREFIX.length()) {
                        String token = trimmed.substring(PROTOCOL_PREFIX.length());
                        attributes.put(WS_TOKEN_ATTR, token);
                        // Echo the chosen subprotocol back — without this the
                        // browser will close the connection on strict UAs.
                        response.getHeaders().set("Sec-WebSocket-Protocol", trimmed);
                        return true;
                    }
                }
            }
        }

        // Transitional fallback: accept the legacy ?wsToken= URL form so
        // already-loaded visitor pages don't break during rollout.
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String legacy = servletRequest.getServletRequest().getParameter("wsToken");
            if (legacy != null && !legacy.isBlank()) {
                log.warn("Visitor WS used legacy URL wsToken — client should send Sec-WebSocket-Protocol: visitor.<token>");
                attributes.put(WS_TOKEN_ATTR, legacy);
                return true;
            }
        }

        log.debug("Visitor WS handshake rejected: no wsToken in Sec-WebSocket-Protocol or query");
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // no-op
    }
}
