package com.zekiloni.george.platform.application.port.out.gateway;

import com.zekiloni.george.platform.domain.model.gateway.smtp.SmtpGateway;
import com.zekiloni.george.platform.domain.model.gateway.smtp.SmtpGatewayProvider;

import java.util.List;

public interface SmtpGatewayPort {
    boolean isSupported(SmtpGatewayProvider type);

    SmtpAccount createSmtpAccount(SmtpGateway gateway, String username, String password, boolean useTls);

    /**
     * Send a batch of messages over a single authenticated SMTP session.
     * One TCP+TLS+AUTH handshake for the whole batch instead of N — cuts
     * per-message latency drastically once outreach lists exceed a few dozen.
     *
     * @return one {@link EmailResult} per input message, in the same order.
     *         Senders correlate back to their domain entity via {@code correlationId}.
     */
    List<EmailResult> sendBatch(SmtpGateway gateway, SmtpCredentials credentials, List<EmailMessage> messages);

    record SmtpAccount(
            String host,
            int port,
            String username,
            String password,
            boolean useTls
    ) {}

    record SmtpCredentials(String username, String password) {}

    record EmailMessage(
            String correlationId,
            String from,
            String to,
            String subject,
            String body
    ) {}

    record EmailResult(
            String correlationId,
            boolean success,
            String error
    ) {
        public static EmailResult ok(String correlationId) { return new EmailResult(correlationId, true, null); }
        public static EmailResult failed(String correlationId, String error) { return new EmailResult(correlationId, false, error); }
    }
}
