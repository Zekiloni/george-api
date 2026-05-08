package com.zekiloni.george.platform.infrastructure.out.integration.gsm;

import com.zekiloni.george.commerce.application.port.out.gateway.GsmProvisioningPort;
import com.zekiloni.george.platform.application.port.out.gateway.GatewayRepositoryPort;
import com.zekiloni.george.platform.application.port.out.gateway.GsmGatewayPort;
import com.zekiloni.george.platform.domain.model.gateway.Gateway;
import com.zekiloni.george.platform.domain.model.gateway.gsm.GsmGateway;
import com.zekiloni.george.platform.domain.model.gateway.gsm.GsmPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GsmProvisioningPortAdapter implements GsmProvisioningPort {

    private final GatewayRepositoryPort gatewayRepository;
    private final List<GsmGatewayPort> gsmGatewayPorts;

    @Override
    public List<GsmPortInfo> listGatewayPorts(String gatewayId) {
        Gateway gateway = gatewayRepository.findById(gatewayId)
                .orElseThrow(() -> new IllegalArgumentException("Gateway not found: " + gatewayId));

        if (!(gateway instanceof GsmGateway gsm)) {
            throw new IllegalArgumentException("Gateway " + gatewayId + " is not a GSM gateway");
        }

        GsmGatewayPort delegate = gsmGatewayPorts.stream()
                .filter(p -> p.isSupported(gsm.getProvider()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No GSM gateway adapter registered for provider: " + gsm.getProvider()));

        return delegate.getAllPortsStatus(gsm).stream()
                .map(GsmProvisioningPortAdapter::toPortInfo)
                .toList();
    }

    private static GsmPortInfo toPortInfo(GsmPort port) {
        String[] parts = port.id().split("\\.");
        int portNum = Integer.parseInt(parts[0]);
        int slot = parts.length > 1 ? Integer.parseInt(parts[1]) : 1;
        boolean available = port.status() != null && port.status().readyToSend();
        return new GsmPortInfo(portNum, slot, available, port.signalStrength(), port.operator());
    }
}
