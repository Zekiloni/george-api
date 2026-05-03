package com.zekiloni.george.commerce.application.port.out.gateway;

import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;

import java.util.Optional;

/**
 * Port for gateway operations needed by the commerce domain.
 * This abstraction allows the commerce domain to interact with gateways
 * without depending on platform domain models directly.
 */
public interface GatewaySelectionPort {

    /**
     * Find the least loaded gateway of the specified type.
     */
    String selectLeastLoadedGateway(String gatewayType);

    /**
     * Find the best gateway based on multiple factors.
     */
    String selectBestGateway(String gatewayType);

    /**
     * Check if a gateway has capacity for more connections.
     */
    boolean hasCapacity(String gatewayId, int requiredCapacity);

    /**
     * Get gateway configuration for service access creation.
     */
    GatewayConfig getGatewayConfig(String gatewayId);

    /**
     * Record successful provisioning.
     */
    void recordSuccess(String gatewayId);

    /**
     * Record failed provisioning.
     */
    void recordFailure(String gatewayId);

    /**
     * Increment active connection count.
     */
    void incrementConnectionCount(String gatewayId);

    /**
     * Decrement active connection count.
     */
    void decrementConnectionCount(String gatewayId);

    /**
     * Gateway configuration DTO.
     */
    record GatewayConfig(
            String gatewayId,
            String type,
            String host,
            int port,
            String provider,
            String fromDomain
    ) {}
}