package com.zekiloni.george.platform.infrastructure.out.messaging.gateway;

import com.zekiloni.george.platform.application.port.out.GatewayDispatchPort;
import com.zekiloni.george.platform.application.port.out.GsmGatewayPort;
import com.zekiloni.george.platform.domain.model.campaign.outreach.Outreach;
import com.zekiloni.george.platform.domain.model.gatway.GatewayType;
import com.zekiloni.george.platform.domain.model.gatway.gsm.GsmGateway;
import com.zekiloni.george.platform.domain.model.gatway.smtp.SmtpGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
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

        // TODO: Implement the logic to send outreach using the GSM gateway port
    }
}
