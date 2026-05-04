package com.zekiloni.george.platform.infrastructure.out.integration.gateway.smtp;

import com.zekiloni.george.platform.application.port.out.gateway.SmtpGatewayPort;
import com.zekiloni.george.platform.domain.model.gateway.GatewayConfigKeys;
import com.zekiloni.george.platform.domain.model.gateway.smtp.SmtpGateway;
import com.zekiloni.george.platform.domain.model.gateway.smtp.SmtpGatewayProvider;
import jakarta.mail.Authenticator;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Component
public class StalwartSmtpGatewayPortAdapter implements SmtpGatewayPort {

    @Override
    public boolean isSupported(SmtpGatewayProvider type) {
        return type == SmtpGatewayProvider.STALWART;
    }

    @Override
    public SmtpAccount createSmtpAccount(SmtpGateway gateway, String username, String password, boolean useTls) {
        Map<String, String> cfg = gateway.getConfig();
        return new SmtpAccount(
                GatewayConfigKeys.string(cfg, GatewayConfigKeys.HOST),
                GatewayConfigKeys.intValue(cfg, GatewayConfigKeys.PORT, 25),
                username,
                password,
                useTls);
    }

    @Override
    public List<EmailResult> sendBatch(SmtpGateway gateway, SmtpCredentials credentials, List<EmailMessage> messages) {
        if (messages == null || messages.isEmpty()) return List.of();

        Map<String, String> cfg = gateway.getConfig();
        String host = GatewayConfigKeys.string(cfg, GatewayConfigKeys.HOST);
        int port = GatewayConfigKeys.intValue(cfg, GatewayConfigKeys.PORT, 25);
        boolean useTls = GatewayConfigKeys.boolValue(cfg, GatewayConfigKeys.USE_TLS, false);

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", String.valueOf(port));
        if (useTls) {
            props.put("mail.smtp.starttls.enable", "true");
        }

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(credentials.username(), credentials.password());
            }
        });

        List<EmailResult> results = new ArrayList<>(messages.size());
        Transport transport = null;
        try {
            transport = session.getTransport("smtp");
            transport.connect(host, port, credentials.username(), credentials.password());
            log.info("Opened SMTP session to {}:{} as {} for batch of {} messages",
                    host, port, credentials.username(), messages.size());

            for (EmailMessage msg : messages) {
                try {
                    MimeMessage mime = new MimeMessage(session);
                    mime.setFrom(new InternetAddress(msg.from()));
                    mime.setRecipients(jakarta.mail.Message.RecipientType.TO, InternetAddress.parse(msg.to()));
                    mime.setSubject(msg.subject());
                    mime.setText(msg.body());
                    mime.saveChanges();
                    transport.sendMessage(mime, mime.getAllRecipients());
                    results.add(EmailResult.ok(msg.correlationId()));
                } catch (MessagingException e) {
                    log.error("Failed to send to {}: {}", maskRecipient(msg.to()), e.getMessage());
                    results.add(EmailResult.failed(msg.correlationId(), e.getMessage()));
                }
            }
        } catch (MessagingException e) {
            // Connection-level failure (auth, TLS, network): all messages fail.
            log.error("SMTP session failed to {}:{} as {}: {}", host, port, credentials.username(), e.getMessage());
            for (EmailMessage msg : messages.subList(results.size(), messages.size())) {
                results.add(EmailResult.failed(msg.correlationId(), "session error: " + e.getMessage()));
            }
        } finally {
            if (transport != null) {
                try { transport.close(); } catch (MessagingException ignored) { /* best-effort close */ }
            }
        }
        return results;
    }

    private String maskRecipient(String recipient) {
        if (recipient == null || recipient.length() <= 4) return "***";
        return recipient.substring(0, 2) + "***" + recipient.substring(recipient.length() - 2);
    }
}
