package com.zekiloni.george.platform.infrastructure.out.integration.gsm.ejoin;

import com.zekiloni.george.platform.application.port.out.gateway.GsmGatewayPort;
import com.zekiloni.george.platform.domain.model.gateway.GatewayConfigKeys;
import com.zekiloni.george.platform.domain.model.gateway.gsm.GsmGateway;
import com.zekiloni.george.platform.domain.model.gateway.gsm.GsmPort;
import com.zekiloni.george.platform.domain.model.gateway.gsm.GsmProvider;
import com.zekiloni.george.platform.infrastructure.out.integration.gsm.ejoin.mapper.EjoinDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EjoinGsmGatewayPortAdapter implements GsmGatewayPort {
    private final EjoinApiClient apiClient;
    private final EjoinDtoMapper mapper;

    @Override
    public boolean isSupported(GsmProvider provider) {
        return provider == GsmProvider.EJOIN;
    }

    @Override
    public GsmPort getPortStatus(GsmGateway gateway, String port) {
        return getAllPortsStatus(gateway).stream()
                .filter(p -> port.equals(p.id()))
                .findFirst()
                .orElseThrow(() -> new EjoinValidationException("No status returned for port " + port));
    }

    @Override
    public List<GsmPort> getAllPortsStatus(GsmGateway gateway) {
        return apiClient.getAllPortStatus(url(gateway), user(gateway), pass(gateway))
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void sendSms(GsmGateway gateway, String port, String phoneNumber, String message) {
        apiClient.sendSms(url(gateway), user(gateway), pass(gateway), port, phoneNumber, message);
    }

    private static String url(GsmGateway g)  { return GatewayConfigKeys.string(g.getConfig(), GatewayConfigKeys.URL); }
    private static String user(GsmGateway g) { return GatewayConfigKeys.string(g.getConfig(), GatewayConfigKeys.USERNAME); }
    private static String pass(GsmGateway g) { return GatewayConfigKeys.string(g.getConfig(), GatewayConfigKeys.PASSWORD); }
}
