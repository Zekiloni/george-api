package com.zekiloni.george.platform.infrastructure.in.ws.bus;

import java.util.function.Consumer;

public interface SessionEventBus {

    void publish(String channel, String message);

    Subscription subscribe(String channel, Consumer<String> listener);

    static String eventsChannel(String sessionId) {
        return "session:" + sessionId + ":events";
    }

    static String actionsChannel(String sessionId) {
        return "session:" + sessionId + ":actions";
    }

    interface Subscription extends AutoCloseable {
        @Override
        void close();
    }
}
