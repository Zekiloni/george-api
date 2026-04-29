package com.zekiloni.george.commerce.infrastructure.out.gateway;

import com.zekiloni.george.commerce.application.port.out.gateway.GatewaySelectionPort;
import com.zekiloni.george.platform.application.port.out.gateway.GatewayRepositoryPort;
import com.zekiloni.george.platform.domain.model.gatway.Gateway;
import com.zekiloni.george.platform.domain.model.gatway.GatewayType;
import com.zekiloni.george.platform.domain.model.gatway.smtp.SmtpGateway;
import com.zekiloni.george.platform.domain.model.gatway.gsm.GsmGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Adapter that implements the commerce gateway selection port
 * by delegating to platform gateway infrastructure.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GatewaySelectionPortAdapter implements GatewaySelectionPort {

    private final GatewayRepositoryPort gatewayRepository;

    // Simple in-memory load tracking
    private final Map<String, AtomicInteger> connectionCounts = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> successCounts = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> failureCounts = new ConcurrentHashMap<>();
    private final Map<String, Long> responseTimeSum = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> responseTimeCount = new ConcurrentHashMap<>();

    @Override
    public String selectLeastLoadedGateway(String gatewayType) {
        GatewayType type = GatewayType.valueOf(gatewayType.toUpperCase());
        List<Gateway> gateways = gatewayRepository.findByType(type);

        if (gateways.isEmpty()) {
            throw new RuntimeException("No available gateways found for type: " + gatewayType);
        }

        Gateway selected = gateways.stream()
                .min(Comparator.comparingInt(g -> getActiveConnectionCount(g.getId())))
                .orElse(gateways.getFirst());

        log.info("Selected least loaded gateway: {} for type: {}", selected.getId(), gatewayType);
        return selected.getId();
    }

    @Override
    public String selectBestGateway(String gatewayType) {
        GatewayType type = GatewayType.valueOf(gatewayType.toUpperCase());
        List<Gateway> gateways = gatewayRepository.findByType(type);

        if (gateways.isEmpty()) {
            throw new RuntimeException("No available gateways found for type: " + gatewayType);
        }

        Gateway selected = gateways.stream()
                .min(this::compareGatewayPerformance)
                .orElse(gateways.getFirst());

        log.info("Selected best gateway: {} for type: {}", selected.getId(), gatewayType);
        return selected.getId();
    }

    @Override
    public boolean hasCapacity(String gatewayId, int requiredCapacity) {
        int currentLoad = getActiveConnectionCount(gatewayId);
        // Assume default max capacity of 100 if not configured
        int maxCapacity = 100;

        boolean hasCapacity = (currentLoad + requiredCapacity) <= maxCapacity;

        if (!hasCapacity) {
            log.warn("Gateway {} at capacity: {}/{}", gatewayId, currentLoad, maxCapacity);
        }

        return hasCapacity;
    }

    @Override
    public GatewayConfig getGatewayConfig(String gatewayId) {
        Gateway gateway = gatewayRepository.findById(gatewayId)
                .orElseThrow(() -> new RuntimeException("Gateway not found: " + gatewayId));

        return switch (gateway) {
            case SmtpGateway smtp -> new GatewayConfig(
                    gateway.getId(),
                    "SMTP",
                    smtp.getHost(),
                    smtp.getPort(),
                    smtp.getProvider().name()
            );
            case GsmGateway gsm -> new GatewayConfig(
                    gateway.getId(),
                    "GSM",
                    gsm.getIpAddress(),
                    gsm.getPort(),
                    gsm.getProvider().name()
            );
            default -> throw new RuntimeException("Unknown gateway type: " + gateway.getClass());
        };
    }

    @Override
    public void recordSuccess(String gatewayId) {
        successCounts.computeIfAbsent(gatewayId, k -> new AtomicInteger(0)).incrementAndGet();
    }

    @Override
    public void recordFailure(String gatewayId) {
        failureCounts.computeIfAbsent(gatewayId, k -> new AtomicInteger(0)).incrementAndGet();
    }

    @Override
    public void incrementConnectionCount(String gatewayId) {
        connectionCounts.computeIfAbsent(gatewayId, k -> new AtomicInteger(0)).incrementAndGet();
    }

    @Override
    public void decrementConnectionCount(String gatewayId) {
        connectionCounts.computeIfAbsent(gatewayId, k -> new AtomicInteger(0)).decrementAndGet();
    }

    private int compareGatewayPerformance(Gateway g1, Gateway g2) {
        // Primary: Load (fewer connections is better)
        int loadCompare = Integer.compare(
                getActiveConnectionCount(g1.getId()),
                getActiveConnectionCount(g2.getId())
        );
        if (loadCompare != 0) return loadCompare;

        // Secondary: Success rate (higher is better)
        int successCompare = Double.compare(
                getSuccessRate(g2.getId()),
                getSuccessRate(g1.getId())
        );
        if (successCompare != 0) return successCompare;

        // Tertiary: Response time (lower is better)
        return Long.compare(
                getAverageResponseTime(g1.getId()),
                getAverageResponseTime(g2.getId())
        );
    }

    private int getActiveConnectionCount(String gatewayId) {
        return connectionCounts.getOrDefault(gatewayId, new AtomicInteger(0)).get();
    }

    private double getSuccessRate(String gatewayId) {
        int successes = successCounts.getOrDefault(gatewayId, new AtomicInteger(0)).get();
        int failures = failureCounts.getOrDefault(gatewayId, new AtomicInteger(0)).get();
        int total = successes + failures;

        return total == 0 ? 1.0 : (double) successes / total;
    }

    private long getAverageResponseTime(String gatewayId) {
        Long sum = responseTimeSum.get(gatewayId);
        Integer count = responseTimeCount.getOrDefault(gatewayId, new AtomicInteger(0)).get();

        return (sum != null && count != null && count > 0) ? sum / count : 0L;
    }
}