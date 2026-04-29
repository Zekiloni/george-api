package com.zekiloni.george.commerce.domain.order.service.strategy;

import com.zekiloni.george.commerce.application.port.out.gateway.GatewaySelectionPort;
import com.zekiloni.george.commerce.application.port.in.ServiceAccessCreateUseCase;
import com.zekiloni.george.commerce.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceStatus;
import com.zekiloni.george.commerce.domain.inventory.model.SmtpServiceAccess;
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
public class SmtpProvisioningStrategy implements ProvisioningStrategy {
    private final ServiceAccessCreateUseCase serviceAccessCreateUseCase;
    private final GatewaySelectionPort gatewaySelectionPort;

    @Override
    public ServiceSpecification getType() {
        return ServiceSpecification.SMTP;
    }

    @Override
    public void provision(Order order, OrderItem orderItem) {
        log.info("Provisioning SMTP service access for order item: {}", orderItem.getId());

        try {
            // 1. Select least loaded SMTP gateway
            String gatewayId = gatewaySelectionPort.selectLeastLoadedGateway("SMTP");
            log.info("Selected gateway {} for SMTP provisioning", gatewayId);

            // 2. Get gateway configuration
            GatewaySelectionPort.GatewayConfig gatewayConfig = gatewaySelectionPort.getGatewayConfig(gatewayId);

            // 3. Generate credentials
            String username = generateUsername(order.getTenantId());
            String password = generatePassword();

            // 4. Call gateway API to create account
            createSmtpAccountOnGateway(gatewayConfig, username, password, orderItem);

            // 5. Create ServiceAccess with provisioning details
            SmtpServiceAccess serviceAccess = createServiceAccess(order, orderItem, gatewayId, username, password, gatewayConfig);
            serviceAccessCreateUseCase.create(serviceAccess);

            // 6. Record success metrics
            gatewaySelectionPort.recordSuccess(gatewayId);
            gatewaySelectionPort.incrementConnectionCount(gatewayId);

            log.info("Successfully provisioned SMTP service access: {} for tenant {}", serviceAccess.getId(), order.getTenantId());

        } catch (Exception e) {
            log.error("Failed to provision SMTP service access: {}", e.getMessage());
            throw new RuntimeException("SMTP provisioning failed", e);
        }
    }

    @Override
    public void deprovision(OrderItem order) {
        log.info("Deprovisioning SMTP service access for order item: {}", order.getId());

        try {
            // Get the gateway ID from the existing service access
            String gatewayId = order.getGatewayId();

            if (gatewayId != null) {
                GatewaySelectionPort.GatewayConfig gatewayConfig = gatewaySelectionPort.getGatewayConfig(gatewayId);

                // Get username from service access
                String username = extractUsername(order);

                // Call gateway to terminate account
                terminateSmtpAccountOnGateway(gatewayConfig, username);

                // Update service access status
                gatewaySelectionPort.decrementConnectionCount(gatewayId);

                log.info("Successfully deprovisioned SMTP service access for order item: {}", order.getId());
            }

        } catch (Exception e) {
            log.error("Failed to deprovision SMTP service access: {}", e.getMessage());
            throw new RuntimeException("SMTP deprovisioning failed", e);
        }
    }

    private SmtpServiceAccess createServiceAccess(Order order, OrderItem orderItem, String gatewayId,
                                                 String username, String password, GatewaySelectionPort.GatewayConfig gatewayConfig) {
        return SmtpServiceAccess.builder()
                .validFrom(OffsetDateTime.now())
                .validTo(getValidTo(orderItem))
                .serviceSpecification(getType())
                .status(ServiceStatus.ACTIVE)
                .characteristic(orderItem.getCharacteristic())
                .orderItem(orderItem)
                .tenantId(order.getTenantId())
                .gatewayId(gatewayId)  // Link to the gateway
                .username(username)
                .password(password)
                .smtpServer(gatewayConfig.host())
                .port(gatewayConfig.port())
                .build();
    }

    private void createSmtpAccountOnGateway(GatewaySelectionPort.GatewayConfig gatewayConfig, String username, String password, OrderItem orderItem) {
        log.debug("Creating SMTP account on gateway {}: username={}", gatewayConfig.gatewayId(), username);

        // TODO: Implement actual Stalwart API call
        // This is where the gateway API is called to create the account
        // Example: call Stalwart admin API to create account with given credentials

        log.warn("Stalwart account creation API call not yet implemented - using mock");
    }

    private void terminateSmtpAccountOnGateway(GatewaySelectionPort.GatewayConfig gatewayConfig, String username) {
        log.debug("Terminating SMTP account {} on gateway {}", username, gatewayConfig.gatewayId());

        // TODO: Implement actual Stalwart API call for account termination
        log.warn("Stalwart account termination API call not yet implemented - using mock");
    }

    private String generateUsername(String tenantId) {
        String randomChars = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "smtp_" + tenantId + "_" + randomChars;
    }

    private String generatePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder password = new StringBuilder(16);
        java.util.Random random = new java.security.SecureRandom();
        for (int i = 0; i < 16; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }

    private String extractUsername(OrderItem order) {
        // Extract username from existing service access or order item
        // This would typically be stored in the service access
        return "extracted_username"; // TODO: Implement proper extraction
    }
}
