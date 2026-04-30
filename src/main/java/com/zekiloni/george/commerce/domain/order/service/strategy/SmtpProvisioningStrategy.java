package com.zekiloni.george.commerce.domain.order.service.strategy;

import com.zekiloni.george.commerce.application.port.in.ServiceAccessCreateUseCase;
import com.zekiloni.george.commerce.application.port.out.gateway.GatewaySelectionPort;
import com.zekiloni.george.commerce.application.port.out.gateway.SmtpProvisioningPort;
import com.zekiloni.george.commerce.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceStatus;
import com.zekiloni.george.commerce.domain.inventory.model.SmtpServiceAccess;
import com.zekiloni.george.commerce.domain.order.model.Order;
import com.zekiloni.george.commerce.domain.order.model.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SmtpProvisioningStrategy implements ProvisioningStrategy {

    private static final String PASSWORD_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    private static final int PASSWORD_LENGTH = 24;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final ServiceAccessCreateUseCase serviceAccessCreateUseCase;
    private final GatewaySelectionPort gatewaySelectionPort;
    private final SmtpProvisioningPort smtpProvisioningPort;

    @Override
    public ServiceSpecification getType() {
        return ServiceSpecification.SMTP;
    }

    @Override
    public void provision(Order order, OrderItem orderItem) {
        log.info("Provisioning SMTP service access for order item: {}", orderItem.getId());

        String gatewayId = gatewaySelectionPort.selectLeastLoadedGateway("SMTP");
        GatewaySelectionPort.GatewayConfig config = gatewaySelectionPort.getGatewayConfig(gatewayId);

        String username = generateUsername(order.getTenantId());
        String password = generatePassword();
        String email = username + "@" + config.host();

        smtpProvisioningPort.createAccount(gatewayId, username, password, email);

        SmtpServiceAccess serviceAccess = SmtpServiceAccess.builder()
                .validFrom(OffsetDateTime.now())
                .validTo(getValidTo(orderItem))
                .serviceSpecification(getType())
                .status(ServiceStatus.ACTIVE)
                .characteristic(orderItem.getCharacteristic())
                .orderItem(orderItem)
                .tenantId(order.getTenantId())
                .gatewayId(gatewayId)
                .username(username)
                .password(password)
                .smtpServer(config.host())
                .port(config.port())
                .build();

        serviceAccessCreateUseCase.create(serviceAccess);
        gatewaySelectionPort.recordSuccess(gatewayId);
        gatewaySelectionPort.incrementConnectionCount(gatewayId);

        log.info("Provisioned SMTP account {} on gateway {} for tenant {}",
                username, gatewayId, order.getTenantId());
    }

    @Override
    public void deprovision(ServiceAccess access) {
        if (!(access instanceof SmtpServiceAccess smtp)) {
            return;
        }
        if (smtp.getGatewayId() == null || smtp.getUsername() == null) {
            log.warn("Cannot deprovision SMTP access {}: missing gatewayId or username", smtp.getId());
            return;
        }

        smtpProvisioningPort.deleteAccount(smtp.getGatewayId(), smtp.getUsername());
        gatewaySelectionPort.decrementConnectionCount(smtp.getGatewayId());

        log.info("Deleted SMTP account {} on gateway {}", smtp.getUsername(), smtp.getGatewayId());
    }

    private String generateUsername(String tenantId) {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        return "smtp_" + tenantId + "_" + suffix;
    }

    private String generatePassword() {
        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            sb.append(PASSWORD_CHARS.charAt(RANDOM.nextInt(PASSWORD_CHARS.length())));
        }
        return sb.toString();
    }
}
