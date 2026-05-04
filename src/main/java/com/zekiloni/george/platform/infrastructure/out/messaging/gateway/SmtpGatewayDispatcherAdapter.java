package com.zekiloni.george.platform.infrastructure.out.messaging.gateway;

import com.zekiloni.george.commerce.application.usecase.ServiceUsageQuotaService;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.commerce.domain.inventory.model.SmtpServiceAccess;
import com.zekiloni.george.platform.application.port.out.gateway.GatewayDispatchPort;
import com.zekiloni.george.platform.application.port.out.gateway.SmtpGatewayPort;
import com.zekiloni.george.platform.application.port.out.gateway.SmtpGatewayPort.EmailMessage;
import com.zekiloni.george.platform.application.port.out.gateway.SmtpGatewayPort.EmailResult;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class SmtpGatewayDispatcherAdapter implements GatewayDispatchPort<SmtpGateway> {
    private final List<SmtpGatewayPort> smtpGatewayPorts;
    private final ServiceUsageQuotaService quotaService;

    @Override
    public boolean isSupported(GatewayType gatewayType) {
        return gatewayType == GatewayType.SMTP;
    }

    @Override
    public void send(List<Outreach> outreach, SmtpGateway gateway) {
        // No tenant ServiceAccess: use the gateway's static credentials and skip quota
        // (legacy/admin path). New campaigns always come in via the ServiceAccess overload.
        SmtpCredentials creds = gatewayCredentials(gateway);
        sendBatchInternal(outreach, gateway, creds, null);
    }

    @Override
    public void send(List<Outreach> outreach, SmtpGateway gateway, ServiceAccess serviceAccess) {
        if (!(serviceAccess instanceof SmtpServiceAccess smtpAccess)) {
            log.warn("Expected SmtpServiceAccess but got {}; falling back to gateway credentials",
                    serviceAccess == null ? "null" : serviceAccess.getClass().getSimpleName());
            send(outreach, gateway);
            return;
        }
        SmtpCredentials creds = new SmtpCredentials(smtpAccess.getUsername(), smtpAccess.getPassword());
        sendBatchInternal(outreach, gateway, creds, smtpAccess);
    }

    private void sendBatchInternal(List<Outreach> outreach,
                                   SmtpGateway gateway,
                                   SmtpCredentials creds,
                                   SmtpServiceAccess access) {
        if (outreach.isEmpty()) return;

        SmtpGatewayPort port = smtpGatewayPorts.stream()
                .filter(p -> p.isSupported(gateway.getProvider()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported SMTP gateway type: " + gateway.getProvider()));

        // Quota: reserve up-front for whatever portion we're allowed to send.
        // Anything beyond the reserved count is marked FAILED (quota exhausted).
        String accessId = access != null ? access.getId() : null;
        int reserved = accessId != null ? quotaService.reserve(accessId, outreach.size()) : outreach.size();
        if (reserved < outreach.size()) {
            log.warn("Quota allowed {} of {} requested for SmtpServiceAccess {}",
                    reserved, outreach.size(), accessId != null ? accessId : "n/a");
            for (Outreach blocked : outreach.subList(reserved, outreach.size())) {
                markStatus(blocked, OutreachStatus.FAILED);
            }
        }
        if (reserved == 0) return;

        List<Outreach> toSend = outreach.subList(0, reserved);
        Map<String, Outreach> byId = new HashMap<>(toSend.size());
        List<EmailMessage> messages = new ArrayList<>(toSend.size());
        String fromDomain = GatewayConfigKeys.string(gateway.getConfig(), GatewayConfigKeys.FROM_DOMAIN);
        String from = fromDomain != null ? creds.username() + "@" + fromDomain : creds.username();

        for (Outreach o : toSend) {
            byId.put(o.getId(), o);
            String email = convertPhoneToEmail(o.getRecipient(), gateway);
            messages.add(new EmailMessage(o.getId(), from, email, "Campaign Message", o.getMessage()));
        }

        String host = GatewayConfigKeys.string(gateway.getConfig(), GatewayConfigKeys.HOST);
        log.info("Dispatching {} messages via {} as {}", messages.size(), host, creds.username());

        List<EmailResult> results = port.sendBatch(gateway, creds, messages);

        int failed = 0;
        for (EmailResult r : results) {
            Outreach o = byId.get(r.correlationId());
            if (o == null) continue;
            markStatus(o, r.success() ? OutreachStatus.SENT : OutreachStatus.FAILED);
            if (!r.success()) failed++;
        }
        if (accessId != null && failed > 0) {
            // Refund the quota we reserved but didn't actually consume.
            quotaService.release(accessId, failed);
        }
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

    private static void markStatus(Outreach outreach, OutreachStatus status) {
        outreach.setStatus(status);
        if (status == OutreachStatus.SENT) outreach.setDispatchedAt(OffsetDateTime.now());
        else if (status == OutreachStatus.FAILED) outreach.setFailedAt(OffsetDateTime.now());
    }
}
