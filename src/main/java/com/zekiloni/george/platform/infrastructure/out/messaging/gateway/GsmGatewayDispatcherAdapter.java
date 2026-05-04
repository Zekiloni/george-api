package com.zekiloni.george.platform.infrastructure.out.messaging.gateway;

import com.zekiloni.george.commerce.application.port.out.InventoryRepositoryPort;
import com.zekiloni.george.commerce.application.usecase.ServiceUsageQuotaService;
import com.zekiloni.george.commerce.domain.inventory.model.GsmServiceAccess;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.platform.application.port.out.gateway.GatewayDispatchPort;
import com.zekiloni.george.platform.application.port.out.gateway.GsmGatewayPort;
import com.zekiloni.george.platform.domain.model.campaign.outreach.Outreach;
import com.zekiloni.george.platform.domain.model.campaign.outreach.OutreachStatus;
import com.zekiloni.george.platform.domain.model.gateway.GatewayConfigKeys;
import com.zekiloni.george.platform.domain.model.gateway.GatewayType;
import com.zekiloni.george.platform.domain.model.gateway.gsm.GsmGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class GsmGatewayDispatcherAdapter implements GatewayDispatchPort<GsmGateway> {
    private final List<GsmGatewayPort> gsmGateway;
    private final InventoryRepositoryPort inventoryRepository;
    private final ServiceUsageQuotaService quotaService;

    @Override
    public boolean isSupported(GatewayType gatewayType) {
        return gatewayType == GatewayType.GSM;
    }

    @Override
    public void send(List<Outreach> outreach, GsmGateway gateway) {
        send(outreach, gateway, null);
    }

    @Override
    public void send(List<Outreach> outreach, GsmGateway gateway, ServiceAccess serviceAccess) {
        GsmGatewayPort port = gsmGateway.stream()
                .filter(p -> p.isSupported(gateway.getProvider()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported GSM provider: " + gateway.getProvider()));

        List<GsmGatewayPort.PortStatus> availablePorts = selectPorts(port, gateway, serviceAccess);
        String ip = GatewayConfigKeys.string(gateway.getConfig(), GatewayConfigKeys.IP_ADDRESS);
        if (availablePorts.isEmpty()) {
            log.error("No GSM ports available for dispatch on gateway {}", ip);
            throw new RuntimeException("No GSM ports available");
        }

        // Quota: reserve up-front for whatever portion this access can still send.
        // Anything beyond the granted count is marked FAILED (quota exhausted).
        String accessId = serviceAccess != null ? serviceAccess.getId() : null;
        int reserved = accessId != null ? quotaService.reserve(accessId, outreach.size()) : outreach.size();
        if (reserved < outreach.size()) {
            log.warn("Quota allowed {} of {} requested for ServiceAccess {}",
                    reserved, outreach.size(), accessId != null ? accessId : "n/a");
            for (Outreach blocked : outreach.subList(reserved, outreach.size())) {
                updateOutreachStatus(blocked, OutreachStatus.FAILED);
            }
        }
        if (reserved == 0) return;

        log.info("Sending {} outreach messages via GSM gateway {} on {} port(s)",
                reserved, ip, availablePorts.size());

        int failed = 0;
        for (int i = 0; i < reserved; i++) {
            Outreach out = outreach.get(i);
            GsmGatewayPort.PortStatus portStatus = availablePorts.get(i % availablePorts.size());

            try {
                sendOutreach(port, gateway, out, portStatus);
                updateOutreachStatus(out, OutreachStatus.SENT);
            } catch (Exception e) {
                log.error("Failed to send outreach via GSM: recipient={}, port={}, error={}",
                        maskRecipient(out.getRecipient()), portStatus.port(), e.getMessage());
                updateOutreachStatus(out, OutreachStatus.FAILED);
                failed++;
            }
        }

        if (accessId != null && failed > 0) {
            // Refund the quota we reserved but didn't actually consume.
            quotaService.release(accessId, failed);
        }
    }

    private List<GsmGatewayPort.PortStatus> selectPorts(GsmGatewayPort port, GsmGateway gateway, ServiceAccess access) {
        List<GsmGatewayPort.PortStatus> active = port.getAllPortsStatus(gateway).stream()
                .filter(GsmGatewayPort.PortStatus::active)
                .filter(GsmGatewayPort.PortStatus::inserted)
                .toList();

        Integer dedicatedPort = (access instanceof GsmServiceAccess gsm && gsm.getPort() != 0)
                ? gsm.getPort() : null;

        if (dedicatedPort != null) {
            return active.stream()
                    .filter(p -> portNumber(p.port()) == dedicatedPort)
                    .toList();
        }

        Set<Integer> taken = inventoryRepository.findAllocatedGsmPorts(gateway.getId());
        return active.stream()
                .filter(p -> !taken.contains(portNumber(p.port())))
                .toList();
    }

    private static int portNumber(String portString) {
        return Integer.parseInt(portString.split("\\.")[0]);
    }

    private void sendOutreach(GsmGatewayPort port, GsmGateway gateway, Outreach outreach,
                              GsmGatewayPort.PortStatus portStatus) {
        log.debug("Sending SMS via GSM: recipient={}, port={}, signal={}",
                maskRecipient(outreach.getRecipient()), portStatus.port(), portStatus.signalStrength());

        port.sendSms(gateway, portStatus.port(), outreach.getRecipient(), outreach.getMessage());
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
}
