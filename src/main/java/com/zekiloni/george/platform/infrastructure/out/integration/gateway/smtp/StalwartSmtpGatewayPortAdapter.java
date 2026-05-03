package com.zekiloni.george.platform.infrastructure.out.integration.gateway.smtp;

import com.zekiloni.george.platform.application.port.out.gateway.SmtpGatewayPort;
import com.zekiloni.george.platform.domain.model.gateway.GatewayConfigKeys;
import com.zekiloni.george.platform.domain.model.gateway.smtp.SmtpGateway;
import com.zekiloni.george.platform.domain.model.gateway.smtp.SmtpGatewayProvider;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
    public void sendEmail(SmtpGateway gateway, String from, String to, String subject, String body) {
        Map<String, String> cfg = gateway.getConfig();
        String host = GatewayConfigKeys.string(cfg, GatewayConfigKeys.HOST);
        int port = GatewayConfigKeys.intValue(cfg, GatewayConfigKeys.PORT, 25);
        boolean useTls = GatewayConfigKeys.boolValue(cfg, GatewayConfigKeys.USE_TLS, false);
        String smtpUser = GatewayConfigKeys.string(cfg, GatewayConfigKeys.SMTP_USERNAME);
        String smtpPass = GatewayConfigKeys.string(cfg, GatewayConfigKeys.SMTP_PASSWORD);

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
                return new PasswordAuthentication(smtpUser, smtpPass);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            log.info("Email sent via STALWART SMTP to {}", maskRecipient(to));
        } catch (MessagingException e) {
            log.error("Failed to send email via STALWART SMTP to {}: {}", maskRecipient(to), e.getMessage());
            throw new RuntimeException("SMTP send failed", e);
        }
    }

    private String maskRecipient(String recipient) {
        if (recipient == null || recipient.length() <= 4) {
            return "***";
        }
        return recipient.substring(0, 2) + "***" + recipient.substring(recipient.length() - 2);
    }
}
