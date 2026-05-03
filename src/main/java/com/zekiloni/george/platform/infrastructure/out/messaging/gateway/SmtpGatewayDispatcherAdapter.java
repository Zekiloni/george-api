package com.zekiloni.george.platform.infrastructure.out.messaging.gateway;

import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.commerce.domain.inventory.model.SmtpServiceAccess;
import com.zekiloni.george.platform.application.port.out.gateway.GatewayDispatchPort;
import com.zekiloni.george.platform.application.port.out.gateway.SmtpGatewayPort;
import com.zekiloni.george.platform.application.port.out.gateway.SmtpGatewayPort.SmtpCredentials;
import com.zekiloni.george.platform.domain.model.campaign.outreach.Outreach;
import com.zekiloni.george.platform.domain.model.campaign.outreach.OutreachStatus;
import com.zekiloni.george.platform.domain.model.gateway.GatewayConfigKeys;
import com.zekiloni.george.platform.domain.model.gateway.GatewayType;
import com.zekiloni.george.platform.domain.model.gateway.smtp.SmtpGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SmtpGatewayDispatcherAdapter implements GatewayDispatchPort<SmtpGateway> {
    private final List<SmtpGatewayPort> smtpGatewayPorts;

    @Override
    public boolean isSupported(GatewayType gatewayType) {
        return gatewayType == GatewayType.SMTP;
    }

    @Override
    public void send(List<Outreach> outreach, SmtpGateway gateway) {
        // No tenant ServiceAccess available — fall back to gateway-config credentials
        // (e.g. legacy "shared SMTP user" gateways). New campaigns always come in via
        // the ServiceAccess overload below.
        SmtpCredentials creds = gatewayCredentials(gateway);
        process(outreach, gateway, creds);
    }

    @Override
    public void send(List<Outreach> outreach, SmtpGateway gateway, ServiceAccess serviceAccess) {
        if (!(serviceAccess instanceof SmtpServiceAccess smtpAccess)) {
            log.warn("Expected SmtpServiceAccess for SMTP dispatch but got {}; falling back to gateway credentials",
                    serviceAccess == null ? "null" : serviceAccess.getClass().getSimpleName());
            send(outreach, gateway);
            return;
        }
        SmtpCredentials creds = new SmtpCredentials(smtpAccess.getUsername(), smtpAccess.getPassword());
        process(outreach, gateway, creds);
    }

    private void process(List<Outreach> outreach, SmtpGateway gateway, SmtpCredentials creds) {
        SmtpGatewayPort port = smtpGatewayPorts.stream()
                .filter(p -> p.isSupported(gateway.getProvider()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported SMTP gateway type: " + gateway.getProvider()));

        String host = GatewayConfigKeys.string(gateway.getConfig(), GatewayConfigKeys.HOST);
        log.info("Sending {} outreach messages via SMTP gateway {} as {}",
                outreach.size(), host, creds.username());

        for (Outreach out : outreach) {
            try {
                sendOutreach(port, gateway, creds, out);
                updateOutreachStatus(out, OutreachStatus.SENT);
            } catch (Exception e) {
                log.error("Failed to send outreach: recipient={}, error={}",
                        maskRecipient(out.getRecipient()), e.getMessage());
                updateOutreachStatus(out, OutreachStatus.FAILED);
            }
        }
    }

    private void sendOutreach(SmtpGatewayPort port, SmtpGateway gateway,
                              SmtpCredentials creds, Outreach outreach) {
        String recipient = outreach.getRecipient();
        String email = convertPhoneToEmail(recipient, gateway);

        log.debug("Converting phone to email for mail2SMS: phone={}, email={}",
                maskRecipient(recipient), maskEmail(email));

        String subject = "Campaign Message";
        String fromDomain = GatewayConfigKeys.string(gateway.getConfig(), GatewayConfigKeys.FROM_DOMAIN);
        String from = fromDomain != null
                ? creds.username() + "@" + fromDomain
                : creds.username();

        port.sendEmail(gateway, creds, from, email, subject, outreach.getMessage());
    }

    private SmtpCredentials gatewayCredentials(SmtpGateway gateway) {
        String user = GatewayConfigKeys.string(gateway.getConfig(), GatewayConfigKeys.SMTP_USERNAME);
        String pass = GatewayConfigKeys.string(gateway.getConfig(), GatewayConfigKeys.SMTP_PASSWORD);
        return new SmtpCredentials(user, pass);
    }

    private String convertPhoneToEmail(String phoneNumber, SmtpGateway gateway) {
        String cleanPhone = phoneNumber.replaceAll("[^0-9+]", "");
        if (cleanPhone.startsWith("+")) {
            cleanPhone = cleanPhone.substring(1);
        }
        String host = GatewayConfigKeys.string(gateway.getConfig(), GatewayConfigKeys.HOST);
        return cleanPhone + "@" + host.replace("smtp.", "mail2sms.");
    }

    private void updateOutreachStatus(Outreach outreach, OutreachStatus status) {
        outreach.setStatus(status);
        if (status == OutreachStatus.SENT) {
            outreach.setDispatchedAt(OffsetDateTime.now());
        } else if (status == OutreachStatus.FAILED) {
            outreach.setFailedAt(OffsetDateTime.now());
        }
    }

    private String maskRecipient(String recipient) {
        if (recipient == null || recipient.length() <= 4) return "***";
        return recipient.substring(0, 2) + "***" + recipient.substring(recipient.length() - 2);
    }

    private String maskEmail(String email) {
        if (email == null || email.isEmpty()) return "***";
        int atIndex = email.indexOf("@");
        if (atIndex <= 0) return "***";
        String localPart = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        if (localPart.length() <= 2) return "***" + domain;
        return localPart.charAt(0) + "***" + localPart.charAt(localPart.length() - 1) + domain;
    }
}
