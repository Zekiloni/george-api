package com.zekiloni.george.platform.infrastructure.out.integration.gateway.smtp.stalwart;

import com.zekiloni.george.commerce.application.port.out.gateway.SmtpProvisioningPort;
import com.zekiloni.george.platform.application.port.out.gateway.GatewayRepositoryPort;
import com.zekiloni.george.platform.domain.model.gateway.Gateway;
import com.zekiloni.george.platform.domain.model.gateway.GatewayConfigKeys;
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
                requireConfig(gateway, GatewayConfigKeys.ADMIN_URL),
                buildAuthHeader(gateway),
                username,
                password,
                email);
    }

    @Override
    public void deleteAccount(String gatewayId, String username) {
        SmtpGateway gateway = loadSmtpGateway(gatewayId);
        stalwartClient.deletePrincipal(
                requireConfig(gateway, GatewayConfigKeys.ADMIN_URL),
                buildAuthHeader(gateway),
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

    /**
     * Stalwart accepts either a Bearer API token or Basic admin credentials.
     * Use whichever the gateway has configured: prefer apiKey if present,
     * fall back to username+password.
     */
    private static String buildAuthHeader(SmtpGateway gateway) {
        String apiKey = GatewayConfigKeys.string(gateway.getConfig(), GatewayConfigKeys.API_KEY);
        if (apiKey != null && !apiKey.isBlank()) {
            return StalwartAdminApiClient.bearer(apiKey);
        }
        String user = GatewayConfigKeys.string(gateway.getConfig(), GatewayConfigKeys.USERNAME);
        String pass = GatewayConfigKeys.string(gateway.getConfig(), GatewayConfigKeys.PASSWORD);
        if (user != null && !user.isBlank() && pass != null && !pass.isBlank()) {
            return StalwartAdminApiClient.basic(user, pass);
        }
        throw new IllegalStateException(
                "Gateway " + gateway.getId() + " has no Stalwart auth configured: set either config.apiKey "
                        + "(Bearer token) or config.username + config.password (Basic auth).");
    }

    private static String requireConfig(SmtpGateway gateway, String key) {
        String value = GatewayConfigKeys.string(gateway.getConfig(), key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Gateway " + gateway.getId() + " is missing config." + key);
        }
        return value;
    }
}
