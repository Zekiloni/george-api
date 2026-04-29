package com.zekiloni.george.commerce.application.service.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class GatewayLoadTracker {

    // Gateway metrics storage
    private final Map<String, GatewayMetrics> metrics = new ConcurrentHashMap<>();

    /**
     * Increment active connection count for a gateway.
     */
    public void incrementConnectionCount(String gatewayId) {
        GatewayMetrics gatewayMetrics = metrics.computeIfAbsent(gatewayId, k -> new GatewayMetrics());
        gatewayMetrics.activeConnections.incrementAndGet();
        log.debug("Incremented connection count for gateway {}: {}",
                gatewayId, gatewayMetrics.activeConnections.get());
    }

    /**
     * Decrement active connection count for a gateway.
     */
    public void decrementConnectionCount(String gatewayId) {
        GatewayMetrics gatewayMetrics = metrics.get(gatewayId);
        if (gatewayMetrics != null) {
            int newCount = gatewayMetrics.activeConnections.decrementAndGet();
            log.debug("Decremented connection count for gateway {}: {}",
                    gatewayId, newCount);
        }
    }

    /**
     * Record successful operation for a gateway.
     */
    public void recordSuccess(String gatewayId, long responseTimeMs) {
        GatewayMetrics gatewayMetrics = metrics.computeIfAbsent(gatewayId, k -> new GatewayMetrics());
        gatewayMetrics.totalOperations.incrementAndGet();
        gatewayMetrics.successfulOperations.incrementAndGet();
        gatewayMetrics.totalResponseTime.addAndGet(responseTimeMs);
    }

    /**
     * Record failed operation for a gateway.
     */
    public void recordFailure(String gatewayId) {
        GatewayMetrics gatewayMetrics = metrics.computeIfAbsent(gatewayId, k -> new GatewayMetrics());
        gatewayMetrics.totalOperations.incrementAndGet();
    }

    /**
     * Get current active connection count for a gateway.
     */
    public int getActiveConnectionCount(String gatewayId) {
        GatewayMetrics gatewayMetrics = metrics.get(gatewayId);
        return gatewayMetrics != null ? gatewayMetrics.activeConnections.get() : 0;
    }

    /**
     * Get success rate for a gateway (percentage).
     */
    public double getSuccessRate(String gatewayId) {
        GatewayMetrics gatewayMetrics = metrics.get(gatewayId);
        if (gatewayMetrics == null || gatewayMetrics.totalOperations.get() == 0) {
            return 100.0; // Assume 100% for new gateways
        }

        int total = gatewayMetrics.totalOperations.get();
        int successful = gatewayMetrics.successfulOperations.get();
        return (successful * 100.0) / total;
    }

    /**
     * Get average response time for a gateway (milliseconds).
     */
    public long getAverageResponseTime(String gatewayId) {
        GatewayMetrics gatewayMetrics = metrics.get(gatewayId);
        if (gatewayMetrics == null || gatewayMetrics.successfulOperations.get() == 0) {
            return 0L;
        }

        long totalTime = gatewayMetrics.totalResponseTime.get();
        int successfulOps = gatewayMetrics.successfulOperations.get();
        return totalTime / successfulOps;
    }

    /**
     * Get maximum capacity for a gateway.
     */
    public int getMaxCapacity(String gatewayId) {
        // Default capacity: 1000 concurrent connections
        return 1000;
    }

    /**
     * Reset metrics for a gateway (useful for testing or manual reset).
     */
    public void resetMetrics(String gatewayId) {
        metrics.remove(gatewayId);
        log.info("Reset metrics for gateway: {}", gatewayId);
    }

    /**
     * Get all gateway metrics for monitoring/dashboard.
     */
    public Map<String, GatewayMetricsSnapshot> getAllMetrics() {
        return metrics.entrySet().stream()
                .collect(ConcurrentHashMap::new,
                        (map, entry) -> map.put(entry.getKey(),
                                new GatewayMetricsSnapshot(
                                        entry.getValue().activeConnections.get(),
                                        entry.getValue().totalOperations.get(),
                                        entry.getValue().successfulOperations.get(),
                                        getSuccessRate(entry.getKey()),
                                        getAverageResponseTime(entry.getKey())
                                )),
                        ConcurrentHashMap::putAll);
    }

    /**
     * Internal metrics storage for a gateway.
     */
    private static class GatewayMetrics {
        AtomicInteger activeConnections = new AtomicInteger(0);
        AtomicInteger totalOperations = new AtomicInteger(0);
        AtomicInteger successfulOperations = new AtomicInteger(0);
        AtomicLong totalResponseTime = new AtomicLong(0);
    }

    /**
     * Snapshot of gateway metrics for reporting.
     */
    public record GatewayMetricsSnapshot(
            int activeConnections,
            int totalOperations,
            int successfulOperations,
            double successRate,
            long averageResponseTime
    ) {}
}