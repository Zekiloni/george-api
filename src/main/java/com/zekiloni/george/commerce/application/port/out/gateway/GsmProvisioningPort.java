package com.zekiloni.george.commerce.application.port.out.gateway;

import java.util.List;

public interface GsmProvisioningPort {

    List<GsmPortInfo> listGatewayPorts(String gatewayId);

    record GsmPortInfo(
            int port,
            int slot,
            boolean available,
            int signalStrength,
            String operator
    ) {}
}
