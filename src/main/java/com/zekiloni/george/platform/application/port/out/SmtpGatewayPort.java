package com.zekiloni.george.platform.application.port.out;

import com.zekiloni.george.platform.domain.model.gatway.smtp.SmtpGateway;
import com.zekiloni.george.platform.domain.model.gatway.smtp.SmtpGatewayType;

public interface SmtpGatewayPort {
    boolean isSupported(SmtpGatewayType type);

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
