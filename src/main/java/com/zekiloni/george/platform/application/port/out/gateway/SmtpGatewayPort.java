package com.zekiloni.george.platform.application.port.out.gateway;

import com.zekiloni.george.platform.domain.model.gateway.smtp.SmtpGateway;
import com.zekiloni.george.platform.domain.model.gateway.smtp.SmtpGatewayProvider;

public interface SmtpGatewayPort {
    boolean isSupported(SmtpGatewayProvider type);

    SmtpAccount createSmtpAccount(SmtpGateway gateway, String username, String password, boolean useTls);

    /**
     * Send a single message authenticated as the tenant's SMTP account.
     *
     * @param gateway      the SMTP gateway (host/port/TLS, no credentials)
     * @param credentials  per-tenant SMTP-AUTH user/pass from the customer's SmtpServiceAccess
     * @param from         envelope from
     * @param to           envelope to
     * @param subject      message subject
     * @param body         plain-text body
     */
    void sendEmail(SmtpGateway gateway, SmtpCredentials credentials,
                   String from, String to, String subject, String body);

    record SmtpAccount(
            String host,
            int port,
            String username,
            String password,
            boolean useTls
    ) {}

    record SmtpCredentials(String username, String password) {}
}
