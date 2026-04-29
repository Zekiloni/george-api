package com.zekiloni.george.platform.infrastructure.out.messaging.gateway;

import com.zekiloni.george.platform.application.port.out.gateway.GatewayDispatchPort;
import com.zekiloni.george.platform.application.port.out.gateway.SmtpGatewayPort;
import com.zekiloni.george.platform.domain.model.campaign.outreach.Outreach;
import com.zekiloni.george.platform.domain.model.campaign.outreach.OutreachStatus;
import com.zekiloni.george.platform.domain.model.gatway.GatewayType;
import com.zekiloni.george.platform.domain.model.gatway.smtp.SmtpGateway;
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
        SmtpGatewayPort port = smtpGatewayPorts.stream()
                .filter(p -> p.isSupported(gateway.getProvider()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported SMTP gateway type: " + gateway.getProvider()));

        log.info("Sending {} outreach messages via SMTP gateway: {}", outreach.size(), gateway.getHost());

        for (Outreach out : outreach) {
            try {
                sendOutreach(port, gateway, out);
                updateOutreachStatus(out, OutreachStatus.SENT);
            } catch (Exception e) {
                log.error("Failed to send outreach: recipient={}, error={}",
                    maskRecipient(out.getRecipient()), e.getMessage());
                updateOutreachStatus(out, OutreachStatus.FAILED);
            }
        }
    }

    private void sendOutreach(SmtpGatewayPort port, SmtpGateway gateway, Outreach outreach) {
        String recipient = outreach.getRecipient();
        String email = convertPhoneToEmail(recipient, gateway);

        log.debug("Converting phone to email for mail2SMS: phone={}, email={}",
            maskRecipient(recipient), maskEmail(email));

        String subject = "Campaign Message";
        String from = gateway.getUsername();

        port.sendEmail(gateway, from, email, subject, outreach.getMessage());
    }

    private String convertPhoneToEmail(String phoneNumber, SmtpGateway gateway) {
        String cleanPhone = phoneNumber.replaceAll("[^0-9+]", "");

        if (cleanPhone.startsWith("+")) {
            cleanPhone = cleanPhone.substring(1);
        }

        return cleanPhone + "@" + gateway.getHost().replace("smtp.", "mail2sms.");
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
        if (recipient == null || recipient.length() <= 4) {
            return "***";
        }
        return recipient.substring(0, 2) + "***" + recipient.substring(recipient.length() - 2);
    }

    private String maskEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "***";
        }
        int atIndex = email.indexOf("@");
        if (atIndex <= 0) {
            return "***";
        }
        String localPart = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        if (localPart.length() <= 2) {
            return "***" + domain;
        }
        return localPart.charAt(0) + "***" + localPart.charAt(localPart.length() - 1) + domain;
    }
}
