package com.zekiloni.george.platform.infrastructure.in.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zekiloni.george.platform.application.port.out.campaign.UserSessionRepositoryPort;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.InteractionType;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserEvent;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSession;
import com.zekiloni.george.platform.infrastructure.in.ws.bus.SessionEventBus;
import com.zekiloni.george.platform.infrastructure.in.ws.session.ConnectedSession;
import com.zekiloni.george.platform.infrastructure.in.ws.session.UserSessionLifecycleService;
import com.zekiloni.george.platform.infrastructure.in.ws.session.UserSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class VisitorWebSocketHandler extends TextWebSocketHandler {

    private static final String SESSION_ID_ATTR = "sessionId";
    private static final String SUBSCRIPTION_ATTR = "actionsSubscription";
    private static final CloseStatus AUTH_FAILED = new CloseStatus(4001, "auth failed");

    private final UserSessionRepositoryPort sessionRepository;
    private final UserSessionRegistry registry;
    private final UserSessionLifecycleService lifecycleService;
    private final SessionEventBus eventBus;
    private final ObjectMapper objectMapper;
    private final AuditorAware<String> auditorAware;

    @Override
    public void afterConnectionEstablished(WebSocketSession ws) throws Exception {
        String wsToken = extractWsToken(ws.getUri());
        if (wsToken == null) {
            ws.close(AUTH_FAILED);
            return;
        }

        UserSession session = sessionRepository.findByWsToken(wsToken).orElse(null);
        if (session == null) {
            ws.close(AUTH_FAILED);
            return;
        }

        String sessionId = session.getId();
        ws.getAttributes().put(SESSION_ID_ATTR, sessionId);
        registry.registerVisitor(sessionId, ws);

        SessionEventBus.Subscription subscription = eventBus.subscribe(
                SessionEventBus.actionsChannel(sessionId),
                msg -> deliverActionToVisitor(ws, sessionId, msg));
        ws.getAttributes().put(SUBSCRIPTION_ATTR, subscription);

        log.info("Visitor connected: sessionId={}", sessionId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession ws, TextMessage message) throws Exception {
        String sessionId = (String) ws.getAttributes().get(SESSION_ID_ATTR);
        if (sessionId == null) {
            ws.close(AUTH_FAILED);
            return;
        }

        ConnectedSession connected = registry.get(sessionId).orElse(null);
        if (connected == null) {
            ws.close(AUTH_FAILED);
            return;
        }

        UserEvent event;
        try {
            event = objectMapper.readValue(message.getPayload(), UserEvent.class);
        } catch (Exception e) {
            log.warn("Malformed event from sessionId={}: {}", sessionId, e.getMessage());
            return;
        }

        if (event.getType() == InteractionType.HEARTBEAT) {
            connected.touchHeartbeat();
            return;
        }

        if (!connected.tryConsumeEventToken()) {
            return;
        }

        stamp(event, false);
        connected.appendEvent(event);
        connected.touchHeartbeat();

        eventBus.publish(SessionEventBus.eventsChannel(sessionId), objectMapper.writeValueAsString(event));

        if (event.getType() == InteractionType.SUBMIT) {
            lifecycleService.markCompleted(sessionId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession ws, CloseStatus status) throws Exception {
        SessionEventBus.Subscription subscription = (SessionEventBus.Subscription) ws.getAttributes().remove(SUBSCRIPTION_ATTR);
        if (subscription != null) {
            subscription.close();
        }
        String sessionId = (String) ws.getAttributes().get(SESSION_ID_ATTR);
        if (sessionId == null) return;
        log.info("Visitor disconnected: sessionId={}, status={}", sessionId, status);
    }

    private void deliverActionToVisitor(WebSocketSession ws, String sessionId, String message) {
        registry.get(sessionId).ifPresent(connected -> {
            try {
                UserEvent action = objectMapper.readValue(message, UserEvent.class);
                if (!connected.getBuffer().contains(action)) {
                    connected.appendEvent(action);
                }
            } catch (Exception e) {
                log.debug("Action message not parseable for buffer append on session {}: {}", sessionId, e.getMessage());
            }
        });
        if (!ws.isOpen()) return;
        try {
            ws.sendMessage(new TextMessage(message));
        } catch (IOException e) {
            log.warn("Failed to deliver action to visitor: {}", e.getMessage());
        }
    }

    private void stamp(UserEvent event, boolean incoming) {
        event.setIncoming(incoming);
        if (event.getId() == null) {
            event.setId(UUID.randomUUID().toString());
        }
        OffsetDateTime now = OffsetDateTime.now();
        if (event.getCreatedAt() == null) {
            event.setCreatedAt(now);
        }
        event.setUpdatedAt(now);
        String auditor = auditorAware.getCurrentAuditor().orElse(null);
        if (event.getCreatedBy() == null) {
            event.setCreatedBy(auditor);
        }
        event.setUpdatedBy(auditor);
    }

    private String extractWsToken(URI uri) {
        if (uri == null || uri.getQuery() == null) return null;
        for (String pair : uri.getQuery().split("&")) {
            int eq = pair.indexOf('=');
            if (eq <= 0) continue;
            if ("wsToken".equals(pair.substring(0, eq))) {
                return pair.substring(eq + 1);
            }
        }
        return null;
    }
}
