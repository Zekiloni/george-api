package com.zekiloni.george.platform.application.port.in.gateway;

import com.zekiloni.george.platform.domain.model.gateway.gsm.GsmPort;

import java.util.List;

public interface GsmGatewayPortsQueryUseCase {
    List<GsmPort> fetchPorts(String gatewayId);
}
