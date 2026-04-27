package com.zekiloni.george.platform.infrastructure.out.messaging.gateway;

import com.zekiloni.george.platform.application.port.out.GatewayDispatchPort;
import com.zekiloni.george.platform.application.port.out.SmtpGatewayPort;
import com.zekiloni.george.platform.domain.model.campaign.outreach.Outreach;
import com.zekiloni.george.platform.domain.model.gatway.GatewayType;
import com.zekiloni.george.platform.domain.model.gatway.smtp.SmtpGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SmtpGatewayDispatcherAdapter implements GatewayDispatchPort<SmtpGateway> {
    private final List<SmtpGatewayPort> smtpGatewayPorts;

    @Override
    public boolean isSupported(GatewayType gatewayType) {
        return gatewayType == GatewayType.SMTP;
    }

    @Override
    public void send(List<Outreach> outreach, SmtpGateway gateway) {
        SmtpGatewayPort port = smtpGatewayPorts.stream()
                .filter(p -> p.isSupported(gateway.getType()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported SMTP gateway type: " + gateway.getType()));
    }
}
