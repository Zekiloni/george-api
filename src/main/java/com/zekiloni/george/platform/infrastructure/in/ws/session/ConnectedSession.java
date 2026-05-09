package com.zekiloni.george.platform.infrastructure.in.ws.session;

import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserEvent;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSessionStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ConnectedSession {

    private static final int BUFFER_CAPACITY = 1000;

    @Getter
    private final String sessionId;

    // Stamped at registration so the operator endpoint can filter the in-memory
    // registry by tenant (avoiding cross-tenant leakage) and by campaign
    // (powering the campaign-detail "Live sessions" tab).
    @Getter
    private final String tenantId;

    @Getter
    private final String campaignId;

    @Getter
    private volatile WebSocketSession visitorSocket;

    @Getter
    private final Deque<UserEvent> buffer = new ArrayDeque<>(BUFFER_CAPACITY);

    @Getter
    private final Set<WebSocketSession> operatorSockets = ConcurrentHashMap.newKeySet();

    @Getter @Setter
    private volatile UserSessionStatus status = UserSessionStatus.ACTIVE;

    @Getter
    private final AtomicLong lastHeartbeatAt = new AtomicLong(Instant.now().toEpochMilli());

    private final RateLimiter eventRateLimiter = new RateLimiter(20, 50);
    private final RateLimiter commandRateLimiter = new RateLimiter(5, 10);

    public ConnectedSession(String sessionId, String tenantId, String campaignId, WebSocketSession visitorSocket) {
        this.sessionId = sessionId;
        this.tenantId = tenantId;
        this.campaignId = campaignId;
        this.visitorSocket = visitorSocket;
    }

    public synchronized void replaceVisitorSocket(WebSocketSession ws) {
        this.visitorSocket = ws;
    }

    public synchronized void appendEvent(UserEvent event) {
        if (buffer.size() >= BUFFER_CAPACITY) {
            buffer.pollFirst();
        }
        buffer.addLast(event);
    }

    public boolean tryConsumeEventToken() {
        return eventRateLimiter.tryConsume();
    }

    public boolean tryConsumeCommandToken() {
        return commandRateLimiter.tryConsume();
    }

    public void touchHeartbeat() {
        lastHeartbeatAt.set(Instant.now().toEpochMilli());
    }
}
