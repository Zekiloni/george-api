package com.zekiloni.george.platform.infrastructure.out.integration.gsm.ejoin;

import com.zekiloni.george.platform.application.port.out.gateway.GsmGatewayPort;
import com.zekiloni.george.platform.domain.model.gatway.gsm.GsmGateway;
import com.zekiloni.george.platform.domain.model.gatway.gsm.GsmProvider;
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
    public PortStatus getPortStatus(GsmGateway gateway, String port) {
        return mapper.toDomain(apiClient.getPortSTatus(gateway.getIpAddress(), gateway.getUsername(), gateway.getPassword(),
                Integer.valueOf(port.split("\\.")[0]), Integer.valueOf(port.split("\\.")[1])));
    }

    @Override
    public List<PortStatus> getAllPortsStatus(GsmGateway gateway) {
        return apiClient
                .getAllPortStatus(gateway.getIpAddress(), gateway.getUsername(), gateway.getPassword())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void sendSms(GsmGateway gateway, String port, String phoneNumber, String message) {
        String[] portParts = port.split("\\.");
        Integer portNum = Integer.valueOf(portParts[0]);
        Integer slotNum = Integer.valueOf(portParts[1]);

        apiClient.sendSms(
            gateway.getIpAddress(),
            gateway.getUsername(),
            gateway.getPassword(),
            portNum,
            slotNum,
            phoneNumber,
            message
        );
    }
}
