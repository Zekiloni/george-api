package com.zekiloni.george.platform.infrastructure.out.integration.gateway.smtp.stalwart;

import com.zekiloni.george.commerce.application.port.out.gateway.SmtpProvisioningPort;
import com.zekiloni.george.platform.application.port.out.gateway.GatewayRepositoryPort;
import com.zekiloni.george.platform.domain.model.gateway.Gateway;
import com.zekiloni.george.platform.domain.model.gateway.smtp.SmtpGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SmtpProvisioningPortAdapter implements SmtpProvisioningPort {

    private final GatewayRepositoryPort gatewayRepository;
    private final StalwartAdminApiClient stalwartClient;

    @Override
    public void createAccount(String gatewayId, String username, String password, String email) {
        SmtpGateway gateway = loadSmtpGateway(gatewayId);
        stalwartClient.createPrincipal(
                gateway.getAdminUrl(),
                gateway.getUsername(),
                gateway.getPassword(),
                username,
                password,
                email);
    }

    @Override
    public void deleteAccount(String gatewayId, String username) {
        SmtpGateway gateway = loadSmtpGateway(gatewayId);
        stalwartClient.deletePrincipal(
                gateway.getAdminUrl(),
                gateway.getUsername(),
                gateway.getPassword(),
                username);
    }

    private SmtpGateway loadSmtpGateway(String gatewayId) {
        Gateway gateway = gatewayRepository.findById(gatewayId)
                .orElseThrow(() -> new IllegalArgumentException("Gateway not found: " + gatewayId));
        if (!(gateway instanceof SmtpGateway smtp)) {
            throw new IllegalArgumentException("Gateway " + gatewayId + " is not an SMTP gateway");
        }
        return smtp;
    }
}
