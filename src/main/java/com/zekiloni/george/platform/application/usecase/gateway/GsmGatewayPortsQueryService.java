package com.zekiloni.george.platform.application.usecase.gateway;

import com.zekiloni.george.platform.application.port.in.gateway.GsmGatewayPortsQueryUseCase;
import com.zekiloni.george.platform.application.port.out.gateway.GatewayRepositoryPort;
import com.zekiloni.george.platform.application.port.out.gateway.GsmGatewayPort;
import com.zekiloni.george.platform.domain.model.gateway.Gateway;
import com.zekiloni.george.platform.domain.model.gateway.gsm.GsmGateway;
import com.zekiloni.george.platform.domain.model.gateway.gsm.GsmPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GsmGatewayPortsQueryService implements GsmGatewayPortsQueryUseCase {

    private final GatewayRepositoryPort gatewayRepository;
    private final List<GsmGatewayPort> adapters;

    @Override
    public List<GsmPort> fetchPorts(String gatewayId) {
        Gateway gateway = gatewayRepository.findById(gatewayId)
                .orElseThrow(() -> new IllegalArgumentException("Gateway not found: " + gatewayId));

        if (!(gateway instanceof GsmGateway gsm)) {
            throw new IllegalArgumentException("Gateway " + gatewayId + " is not a GSM gateway");
        }

        return adapters.stream()
                .filter(a -> a.isSupported(gsm.getProvider()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No GSM adapter registered for provider: " + gsm.getProvider()))
                .getAllPortsStatus(gsm);
    }
}
