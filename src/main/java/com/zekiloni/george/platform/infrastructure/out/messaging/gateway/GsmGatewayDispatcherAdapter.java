package com.zekiloni.george.platform.infrastructure.out.messaging.gateway;

import com.zekiloni.george.platform.application.port.out.gateway.GatewayDispatchPort;
import com.zekiloni.george.platform.application.port.out.gateway.GsmGatewayPort;
import com.zekiloni.george.platform.domain.model.campaign.outreach.Outreach;
import com.zekiloni.george.platform.domain.model.campaign.outreach.OutreachStatus;
import com.zekiloni.george.platform.domain.model.gateway.GatewayType;
import com.zekiloni.george.platform.domain.model.gateway.gsm.GsmGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GsmGatewayDispatcherAdapter implements GatewayDispatchPort<GsmGateway> {
    private final List<GsmGatewayPort> gsmGateway;

    @Override
    public boolean isSupported(GatewayType gatewayType) {
        return gatewayType == GatewayType.GSM;
    }

    @Override
    public void send(List<Outreach> outreach, GsmGateway gateway) {
        GsmGatewayPort port = gsmGateway.stream()
                .filter(p -> p.isSupported(gateway.getProvider()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported GSM provider: " + gateway.getProvider()));

        log.info("Sending {} outreach messages via GSM gateway: {}", outreach.size(), gateway.getIpAddress());

        // Get available slots
        List<GsmGatewayPort.PortStatus> availablePorts = port.getAllPortsStatus(gateway)
                .stream()
                .filter(GsmGatewayPort.PortStatus::active)
                .filter(GsmGatewayPort.PortStatus::inserted)
                .toList();

        if (availablePorts.isEmpty()) {
            log.error("No active GSM ports available for gateway: {}", gateway.getIpAddress());
            throw new RuntimeException("No active GSM ports available");
        }

        log.info("Found {} active GSM ports out of {} total", availablePorts.size(), gateway.getTotalPort());

        // Distribute outreaches across available slots using round-robin
        for (int i = 0; i < outreach.size(); i++) {
            Outreach out = outreach.get(i);
            GsmGatewayPort.PortStatus portStatus = availablePorts.get(i % availablePorts.size());

            try {
                sendOutreach(port, gateway, out, portStatus);
                updateOutreachStatus(out, OutreachStatus.SENT);
            } catch (Exception e) {
                log.error("Failed to send outreach via GSM: recipient={}, port={}, error={}",
                        maskRecipient(out.getRecipient()), portStatus.port(), e.getMessage());
                updateOutreachStatus(out, OutreachStatus.FAILED);
            }
        }
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
