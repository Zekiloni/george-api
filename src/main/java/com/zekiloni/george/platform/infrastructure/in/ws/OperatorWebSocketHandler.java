package com.zekiloni.george.platform.infrastructure.in.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserEvent;
import com.zekiloni.george.platform.infrastructure.in.ws.session.ConnectedSession;
import com.zekiloni.george.platform.infrastructure.in.ws.session.UserSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;

@Component
@RequiredArgsConstructor
@Slf4j
public class OperatorWebSocketHandler extends TextWebSocketHandler {

    private static final String SESSION_ID_ATTR = "sessionId";
    private static final CloseStatus SESSION_NOT_FOUND = new CloseStatus(4004, "session not found");

    private final UserSessionRegistry registry;
    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession ws) throws Exception {
        String sessionId = extractSessionId(ws.getUri());
        if (sessionId == null) {
            ws.close(SESSION_NOT_FOUND);
            return;
        }

        ConnectedSession connected = registry.get(sessionId).orElse(null);
        if (connected == null) {
            ws.close(SESSION_NOT_FOUND);
            return;
        }

        ws.getAttributes().put(SESSION_ID_ATTR, sessionId);
        registry.subscribeOperator(sessionId, ws);
        replayBuffer(ws, connected);
        log.info("Operator subscribed: sessionId={}", sessionId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession ws, CloseStatus status) throws Exception {
        registry.unsubscribeOperator(ws);
        String sessionId = (String) ws.getAttributes().get(SESSION_ID_ATTR);
        log.info("Operator unsubscribed: sessionId={}, status={}", sessionId, status);
    }

    private void replayBuffer(WebSocketSession ws, ConnectedSession connected) {
        for (UserEvent buffered : connected.getBuffer()) {
            try {
                ws.sendMessage(new TextMessage(objectMapper.writeValueAsString(buffered)));
            } catch (IOException e) {
                log.warn("Failed to replay buffered event to operator: {}", e.getMessage());
                return;
            }
        }
    }

    private String extractSessionId(URI uri) {
        if (uri == null || uri.getQuery() == null) return null;
        for (String pair : uri.getQuery().split("&")) {
            int eq = pair.indexOf('=');
            if (eq <= 0) continue;
            if ("sessionId".equals(pair.substring(0, eq))) {
                return pair.substring(eq + 1);
            }
        }
        return null;
    }
}
