package com.zekiloni.george.platform.infrastructure.in.ws.bus;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Component
@ConditionalOnProperty(prefix = "app.event-bus", name = "type", havingValue = "redis")
@Slf4j
public class RedisSessionEventBus implements SessionEventBus {

    private final StringRedisTemplate redisTemplate;
    private final RedisMessageListenerContainer listenerContainer;
    private final Map<String, ChannelHolder> channels = new ConcurrentHashMap<>();

    public RedisSessionEventBus(StringRedisTemplate redisTemplate,
                                RedisMessageListenerContainer listenerContainer) {
        this.redisTemplate = redisTemplate;
        this.listenerContainer = listenerContainer;
    }

    @Override
    public void publish(String channel, String message) {
        redisTemplate.convertAndSend(channel, message);
    }

    @Override
    public Subscription subscribe(String channel, Consumer<String> listener) {
        ChannelHolder holder = channels.computeIfAbsent(channel, this::createHolder);
        holder.listeners.add(listener);
        return () -> {
            holder.listeners.remove(listener);
            if (holder.listeners.isEmpty()) {
                channels.remove(channel, holder);
                listenerContainer.removeMessageListener(holder.dispatcher, holder.topic);
            }
        };
    }

    private ChannelHolder createHolder(String channel) {
        ChannelTopic topic = new ChannelTopic(channel);
        Set<Consumer<String>> listeners = ConcurrentHashMap.newKeySet();
        MessageListener dispatcher = (message, pattern) -> {
            String body = new String(message.getBody(), StandardCharsets.UTF_8);
            for (Consumer<String> l : listeners) {
                try {
                    l.accept(body);
                } catch (Exception e) {
                    log.warn("Redis listener on channel {} threw: {}", channel, e.getMessage());
                }
            }
        };
        listenerContainer.addMessageListener(dispatcher, topic);
        return new ChannelHolder(topic, dispatcher, listeners);
    }

    private record ChannelHolder(ChannelTopic topic,
                                 MessageListener dispatcher,
                                 Set<Consumer<String>> listeners) {}
}
