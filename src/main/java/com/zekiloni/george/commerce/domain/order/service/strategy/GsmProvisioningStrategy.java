package com.zekiloni.george.commerce.domain.order.service.strategy;

import com.zekiloni.george.commerce.application.port.out.gateway.GatewaySelectionPort;
import com.zekiloni.george.commerce.application.port.in.ServiceAccessCreateUseCase;
import com.zekiloni.george.commerce.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.commerce.domain.inventory.model.GsmServiceAccess;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceStatus;
import com.zekiloni.george.commerce.domain.order.model.Order;
import com.zekiloni.george.commerce.domain.order.model.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class GsmProvisioningStrategy implements ProvisioningStrategy {
    private final ServiceAccessCreateUseCase serviceAccessCreateUseCase;
    private final GatewaySelectionPort gatewaySelectionPort;

    @Override
    public ServiceSpecification getType() {
        return ServiceSpecification.GSM;
    }

    @Override
    public void provision(Order order, OrderItem orderItem) {
        log.info("Provisioning GSM service access for order item: {}", orderItem.getId());

        try {
            // 1. Select least loaded GSM gateway
            String gatewayId = gatewaySelectionPort.selectLeastLoadedGateway("GSM");
            log.info("Selected gateway {} for GSM provisioning", gatewayId);

            // 2. Get gateway configuration
            GatewaySelectionPort.GatewayConfig gatewayConfig = gatewaySelectionPort.getGatewayConfig(gatewayId);

            int port = 0; // TODO: Determine port number based on order item characteristics or gateway response
            // if order item characteristic dedicated port, extract port number
            // int port = order.getPort(); // Assuming port is stored in order item or service access
            // 4. Call gateway API to provision access
            provisionGsmAccessOnGateway(gatewayConfig, orderItem);

            // 5. Create ServiceAccess with provisioning details
            GsmServiceAccess serviceAccess = createServiceAccess(order, orderItem, gatewayId, port);
            serviceAccessCreateUseCase.create(serviceAccess);

            // 6. Record success metrics
            gatewaySelectionPort.recordSuccess(gatewayId);
            gatewaySelectionPort.incrementConnectionCount(gatewayId);

            log.info("Successfully provisioned GSM service access: {} for tenant {}", serviceAccess.getId(), order.getTenantId());

        } catch (Exception e) {
            log.error("Failed to provision GSM service access: {}", e.getMessage());
            throw new RuntimeException("GSM provisioning failed", e);
        }
    }

    @Override
    public void deprovision(OrderItem order) {
        log.info("Deprovisioning GSM service access for order item: {}", order.getId());

        try {
            // Get the gateway ID from the existing service access
            String gatewayId = order.getGatewayId();

            if (gatewayId != null) {
                GatewaySelectionPort.GatewayConfig gatewayConfig = gatewaySelectionPort.getGatewayConfig(gatewayId);

                // Get accessId from service access
                String accessId = extractAccessId(order);

                // Call gateway to terminate access
                terminateGsmAccessOnGateway(gatewayConfig, accessId);

                // Update service access status
                gatewaySelectionPort.decrementConnectionCount(gatewayId);

                log.info("Successfully deprovisioned GSM service access for order item: {}", order.getId());
            }

        } catch (Exception e) {
            log.error("Failed to deprovision GSM service access: {}", e.getMessage());
            throw new RuntimeException("GSM deprovisioning failed", e);
        }
    }

    private GsmServiceAccess createServiceAccess(Order order, OrderItem orderItem, String gatewayId, int port) {
        return GsmServiceAccess.builder()
                .validFrom(OffsetDateTime.now())
                .validTo(getValidTo(orderItem))
                .serviceSpecification(getType())
                .status(ServiceStatus.ACTIVE)
                .characteristic(orderItem.getCharacteristic())
                .orderItem(orderItem)
                .tenantId(order.getTenantId())
                .gatewayId(gatewayId)  // Link to the gateway
                .port(port)
                .build();
    }

    private void provisionGsmAccessOnGateway(GatewaySelectionPort.GatewayConfig gatewayConfig, OrderItem orderItem) {
        log.debug("Provisioning GSM access on gateway {}", gatewayConfig.gatewayId());

        // TODO: Implement actual GSM gateway API call (e.g., EJOIN API)
        // This is where the gateway API is called to provision access
        // Example: call EJOIN to see if port is available, then provision and get assigned port number

        log.warn("GSM access provisioning API call not yet implemented - using mock");
    }

    private void terminateGsmAccessOnGateway(GatewaySelectionPort.GatewayConfig gatewayConfig, String accessId) {
        log.debug("Terminating GSM access {} on gateway {}", accessId, gatewayConfig.gatewayId());

        // TODO: Implement actual GSM gateway API call for access termination
        log.warn("GSM access termination API call not yet implemented - using mock");
    }

    private String generateApiKey() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder apiKey = new StringBuilder(32);
        java.util.Random random = new java.security.SecureRandom();
        for (int i = 0; i < 32; i++) {
            apiKey.append(chars.charAt(random.nextInt(chars.length())));
        }
        return apiKey.toString();
    }

    private String extractAccessId(OrderItem order) {
        // Extract accessId from existing service access or order item
        // This would typically be stored in the service access
        return "extracted_access_id"; // TODO: Implement proper extraction
    }
}