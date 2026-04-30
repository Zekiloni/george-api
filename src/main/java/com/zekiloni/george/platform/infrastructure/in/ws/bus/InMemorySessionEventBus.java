package com.zekiloni.george.platform.infrastructure.in.ws.bus;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Component
@ConditionalOnProperty(prefix = "app.event-bus", name = "type", havingValue = "memory", matchIfMissing = true)
@Slf4j
public class InMemorySessionEventBus implements SessionEventBus {

    private final Map<String, Set<Consumer<String>>> subscribers = new ConcurrentHashMap<>();

    @Override
    public void publish(String channel, String message) {
        Set<Consumer<String>> listeners = subscribers.get(channel);
        if (listeners == null) return;
        for (Consumer<String> listener : listeners) {
            try {
                listener.accept(message);
            } catch (Exception e) {
                log.warn("Listener on channel {} threw: {}", channel, e.getMessage());
            }
        }
    }

    @Override
    public Subscription subscribe(String channel, Consumer<String> listener) {
        subscribers.computeIfAbsent(channel, k -> ConcurrentHashMap.newKeySet()).add(listener);
        return () -> {
            Set<Consumer<String>> listeners = subscribers.get(channel);
            if (listeners != null) {
                listeners.remove(listener);
                if (listeners.isEmpty()) {
                    subscribers.remove(channel, listeners);
                }
            }
        };
    }
}
