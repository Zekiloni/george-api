package com.zekiloni.george.platform.application.port.out.gateway;

import com.zekiloni.george.platform.domain.model.gatway.smtp.SmtpGateway;
import com.zekiloni.george.platform.domain.model.gatway.smtp.SmtpGatewayProvider;

public interface SmtpGatewayPort {
    boolean isSupported(SmtpGatewayProvider type);

    SmtpAccount createSmtpAccount(SmtpGateway gateway, String username, String password, boolean useTls);

    void sendEmail(SmtpGateway gateway, String from, String to, String subject, String body);

    record SmtpAccount(
            String host,
            int port,
            String username,
            String password,
            boolean useTls
    ) {
    }
}
