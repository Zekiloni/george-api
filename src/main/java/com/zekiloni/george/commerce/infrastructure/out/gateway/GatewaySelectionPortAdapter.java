package com.zekiloni.george.commerce.infrastructure.out.gateway;

import com.zekiloni.george.commerce.application.port.out.gateway.GatewaySelectionPort;
import com.zekiloni.george.commerce.application.service.gateway.GatewayLoadTracker;
import com.zekiloni.george.platform.application.port.out.gateway.GatewayRepositoryPort;
import com.zekiloni.george.platform.domain.model.gateway.Gateway;
import com.zekiloni.george.platform.domain.model.gateway.GatewayConfigKeys;
import com.zekiloni.george.platform.domain.model.gateway.GatewayType;
import com.zekiloni.george.platform.domain.model.gateway.smtp.SmtpGateway;
import com.zekiloni.george.platform.domain.model.gateway.gsm.GsmGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GatewaySelectionPortAdapter implements GatewaySelectionPort {

    private final GatewayRepositoryPort gatewayRepository;
    private final GatewayLoadTracker loadTracker;

    @Override
    public String selectLeastLoadedGateway(String gatewayType) {
        GatewayType type = GatewayType.valueOf(gatewayType.toUpperCase());
        List<Gateway> gateways = gatewayRepository.findByType(type);

        if (gateways.isEmpty()) {
            throw new RuntimeException("No available gateways found for type: " + gatewayType);
        }

        Gateway selected = gateways.stream()
                .min(Comparator.comparingInt(g -> loadTracker.getActiveConnectionCount(g.getId())))
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
        int currentLoad = loadTracker.getActiveConnectionCount(gatewayId);
        int maxCapacity = loadTracker.getMaxCapacity(gatewayId);

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
                    GatewayConfigKeys.urlHost(smtp.getConfig()),
                    GatewayConfigKeys.urlPort(smtp.getConfig(), 0),
                    smtp.getProvider().name(),
                    GatewayConfigKeys.string(smtp.getConfig(), GatewayConfigKeys.FROM_DOMAIN)
            );
            case GsmGateway gsm -> new GatewayConfig(
                    gateway.getId(),
                    "GSM",
                    GatewayConfigKeys.urlHost(gsm.getConfig()),
                    GatewayConfigKeys.urlPort(gsm.getConfig(), 0),
                    gsm.getProvider().name(),
                    null
            );
            default -> throw new RuntimeException("Unknown gateway type: " + gateway.getClass());
        };
    }

    @Override
    public void recordSuccess(String gatewayId) {
        loadTracker.recordSuccess(gatewayId, 0);
    }

    @Override
    public void recordFailure(String gatewayId) {
        loadTracker.recordFailure(gatewayId);
    }

    @Override
    public void incrementConnectionCount(String gatewayId) {
        loadTracker.incrementConnectionCount(gatewayId);
    }

    @Override
    public void decrementConnectionCount(String gatewayId) {
        loadTracker.decrementConnectionCount(gatewayId);
    }

    private int compareGatewayPerformance(Gateway g1, Gateway g2) {
        int loadCompare = Integer.compare(
                loadTracker.getActiveConnectionCount(g1.getId()),
                loadTracker.getActiveConnectionCount(g2.getId())
        );
        if (loadCompare != 0) return loadCompare;

        int successCompare = Double.compare(
                loadTracker.getSuccessRate(g2.getId()),
                loadTracker.getSuccessRate(g1.getId())
        );
        if (successCompare != 0) return successCompare;

        return Long.compare(
                loadTracker.getAverageResponseTime(g1.getId()),
                loadTracker.getAverageResponseTime(g2.getId())
        );
    }
}
