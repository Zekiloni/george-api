package com.zekiloni.george.platform.infrastructure.out.messaging.gateway;

import com.zekiloni.george.commerce.application.port.out.InventoryRepositoryPort;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.commerce.domain.inventory.model.SmtpServiceAccess;
import com.zekiloni.george.platform.application.port.out.campaign.OutreachRepositoryPort;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class SmtpGatewayDispatcherAdapter implements GatewayDispatchPort<SmtpGateway> {
    private final List<SmtpGatewayPort> smtpGatewayPorts;
    private final InventoryRepositoryPort inventoryRepository;
    private final OutreachRepositoryPort outreachRepository;
    private final Mail2SmsResolver mail2SmsResolver;
    private final int defaultDailyLimit;

    public SmtpGatewayDispatcherAdapter(List<SmtpGatewayPort> smtpGatewayPorts,
                                        InventoryRepositoryPort inventoryRepository,
                                        OutreachRepositoryPort outreachRepository,
                                        Mail2SmsResolver mail2SmsResolver,
                                        @Value("${app.usage.default-daily-limit:50000}") int defaultDailyLimit) {
        this.smtpGatewayPorts = smtpGatewayPorts;
        this.inventoryRepository = inventoryRepository;
        this.outreachRepository = outreachRepository;
        this.mail2SmsResolver = mail2SmsResolver;
        this.defaultDailyLimit = defaultDailyLimit;
    }

    @Override
    public boolean isSupported(GatewayType gatewayType) {
        return gatewayType == GatewayType.SMTP;
    }

    @Override
    public void send(List<Outreach> outreach, SmtpGateway gateway) {
        // No tenant ServiceAccess: use the gateway's static credentials and skip quota
        // (legacy/admin path). New campaigns always come in via the ServiceAccess overload.
        SmtpCredentials creds = gatewayCredentials(gateway);
        sendBatchInternal(outreach, gateway, creds);
    }

    @Override
    @Transactional
    public void send(List<Outreach> outreach, SmtpGateway gateway, ServiceAccess serviceAccess) {
        if (!(serviceAccess instanceof SmtpServiceAccess smtpAccess)) {
            log.warn("Expected SmtpServiceAccess but got {}; falling back to gateway credentials",
                    serviceAccess == null ? "null" : serviceAccess.getClass().getSimpleName());
            send(outreach, gateway);
            return;
        }
        if (outreach.isEmpty()) return;

        int granted = computeGranted(serviceAccess, outreach.size());
        if (granted < outreach.size()) {
            log.warn("Quota allowed {} of {} requested for SmtpServiceAccess {}",
                    granted, outreach.size(), serviceAccess.getId());
            for (Outreach blocked : outreach.subList(granted, outreach.size())) {
                markStatus(blocked, OutreachStatus.FAILED);
            }
        }
        if (granted == 0) return;

        SmtpCredentials creds = new SmtpCredentials(smtpAccess.getUsername(), smtpAccess.getPassword());
        sendBatchInternal(outreach.subList(0, granted), gateway, creds);
    }

    private int computeGranted(ServiceAccess access, int requested) {
        ServiceAccess locked = inventoryRepository.lockById(access.getId())
                .orElseThrow(() -> new IllegalStateException("ServiceAccess not found: " + access.getId()));

        OffsetDateTime startOfDayUtc = LocalDate.now(ZoneOffset.UTC).atStartOfDay().atOffset(ZoneOffset.UTC);
        long dailyLimit = locked.getDailyLimit().map(Integer::longValue).orElse((long) defaultDailyLimit);
        long usedToday = outreachRepository.countDispatchedSinceByServiceAccessId(locked.getId(), startOfDayUtc);
        long dailyRemaining = Math.max(0, dailyLimit - usedToday);

        long lifetimeRemaining = locked.getMessageQuota().map(quota -> {
            long usedLifetime = outreachRepository.countDispatchedSinceByServiceAccessId(
                    locked.getId(), locked.getValidFrom());
            return Math.max(0L, quota - usedLifetime);
        }).orElse(Long.MAX_VALUE);

        return (int) Math.min(requested, Math.min(dailyRemaining, lifetimeRemaining));
    }

    private void sendBatchInternal(List<Outreach> outreach,
                                   SmtpGateway gateway,
                                   SmtpCredentials creds) {
        if (outreach.isEmpty()) return;

        SmtpGatewayPort port = smtpGatewayPorts.stream()
                .filter(p -> p.isSupported(gateway.getProvider()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported SMTP gateway type: " + gateway.getProvider()));

        Map<String, Outreach> byId = new HashMap<>(outreach.size());
        List<EmailMessage> messages = new ArrayList<>(outreach.size());
        String fromDomain = GatewayConfigKeys.string(gateway.getConfig(), GatewayConfigKeys.FROM_DOMAIN);
        String from = fromDomain != null ? creds.username() + "@" + fromDomain : creds.username();

        for (Outreach o : outreach) {
            byId.put(o.getId(), o);
            String email = resolveRecipientAddress(o, gateway);
            messages.add(new EmailMessage(o.getId(), from, email, "Campaign Message", o.getMessage()));
        }

        String host = GatewayConfigKeys.string(gateway.getConfig(), GatewayConfigKeys.HOST);
        log.info("Dispatching {} messages via {} as {}", messages.size(), host, creds.username());

        List<EmailResult> results = port.sendBatch(gateway, creds, messages);

        for (EmailResult r : results) {
            Outreach o = byId.get(r.correlationId());
            if (o == null) continue;
            markStatus(o, r.success() ? OutreachStatus.SENT : OutreachStatus.FAILED);
        }
    }

    private SmtpCredentials gatewayCredentials(SmtpGateway gateway) {
        String user = GatewayConfigKeys.string(gateway.getConfig(), GatewayConfigKeys.SMTP_USERNAME);
        String pass = GatewayConfigKeys.string(gateway.getConfig(), GatewayConfigKeys.SMTP_PASSWORD);
        return new SmtpCredentials(user, pass);
    }

    private String resolveRecipientAddress(Outreach outreach, SmtpGateway gateway) {
        String cleanPhone = outreach.getRecipient().replaceAll("[^0-9+]", "");
        if (cleanPhone.startsWith("+")) {
            cleanPhone = cleanPhone.substring(1);
        }
        String domain = mail2SmsResolver.resolve(outreach.getCountry(), outreach.getCarrier())
                .orElseGet(() -> {
                    String fallback = GatewayConfigKeys.string(gateway.getConfig(), GatewayConfigKeys.HOST)
                            .replace("smtp.", "mail2sms.");
                    log.warn("No mail2sms mapping for country={} carrier={} on outreach {}; falling back to {}",
                            outreach.getCountry(), outreach.getCarrier(), outreach.getId(), fallback);
                    return fallback;
                });
        return cleanPhone + "@" + domain;
    }

    private static void markStatus(Outreach outreach, OutreachStatus status) {
        outreach.setStatus(status);
        if (status == OutreachStatus.SENT) outreach.setDispatchedAt(OffsetDateTime.now());
        else if (status == OutreachStatus.FAILED) outreach.setFailedAt(OffsetDateTime.now());
    }
}
