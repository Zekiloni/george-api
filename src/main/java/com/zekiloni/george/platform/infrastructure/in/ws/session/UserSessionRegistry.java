package com.zekiloni.george.platform.infrastructure.in.ws.session;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class UserSessionRegistry {

    private final Map<String, ConnectedSession> bySessionId = new ConcurrentHashMap<>();

    public ConnectedSession registerVisitor(String sessionId, WebSocketSession ws) {
        return bySessionId.compute(sessionId, (id, existing) -> {
            if (existing != null) {
                kickOldVisitor(existing);
                existing.replaceVisitorSocket(ws);
                return existing;
            }
            return new ConnectedSession(sessionId, ws);
        });
    }

    public Optional<ConnectedSession> get(String sessionId) {
        return Optional.ofNullable(bySessionId.get(sessionId));
    }

    public Optional<ConnectedSession> findByVisitorSocket(WebSocketSession ws) {
        return bySessionId.values().stream()
                .filter(s -> ws.equals(s.getVisitorSocket()))
                .findFirst();
    }

    public Optional<ConnectedSession> remove(String sessionId) {
        return Optional.ofNullable(bySessionId.remove(sessionId));
    }

    public Collection<ConnectedSession> snapshot() {
        return bySessionId.values();
    }

    public void subscribeOperator(String sessionId, WebSocketSession operatorWs) {
        ConnectedSession session = bySessionId.get(sessionId);
        if (session != null) {
            session.getOperatorSockets().add(operatorWs);
        }
    }

    public void unsubscribeOperator(WebSocketSession operatorWs) {
        bySessionId.values().forEach(s -> s.getOperatorSockets().remove(operatorWs));
    }

    private void kickOldVisitor(ConnectedSession existing) {
        WebSocketSession old = existing.getVisitorSocket();
        if (old == null || !old.isOpen()) return;
        try {
            old.close(new CloseStatus(4000, "kicked: another tab opened"));
        } catch (IOException e) {
            log.warn("Failed to close kicked visitor socket: {}", e.getMessage());
        }
    }
}
