package com.zekiloni.george.platform.infrastructure.in.ws.session;

import java.util.concurrent.atomic.AtomicLong;

public class RateLimiter {

    private final long refillPerSecond;
    private final long capacity;
    private final AtomicLong tokens;
    private final AtomicLong lastRefillAt;

    public RateLimiter(long refillPerSecond, long capacity) {
        this.refillPerSecond = refillPerSecond;
        this.capacity = capacity;
        this.tokens = new AtomicLong(capacity);
        this.lastRefillAt = new AtomicLong(System.nanoTime());
    }

    public boolean tryConsume() {
        refill();
        long current;
        do {
            current = tokens.get();
            if (current <= 0) return false;
        } while (!tokens.compareAndSet(current, current - 1));
        return true;
    }

    private void refill() {
        long now = System.nanoTime();
        long last = lastRefillAt.get();
        long elapsedNanos = now - last;
        long minStep = 1_000_000_000L / refillPerSecond;
        if (elapsedNanos < minStep) return;
        if (!lastRefillAt.compareAndSet(last, now)) return;

        long add = (elapsedNanos * refillPerSecond) / 1_000_000_000L;
        if (add <= 0) return;
        long current;
        long updated;
        do {
            current = tokens.get();
            updated = Math.min(capacity, current + add);
        } while (!tokens.compareAndSet(current, updated));
    }
}
