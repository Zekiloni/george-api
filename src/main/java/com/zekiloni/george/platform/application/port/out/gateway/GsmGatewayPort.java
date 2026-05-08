package com.zekiloni.george.platform.application.port.out.gateway;

import com.zekiloni.george.platform.domain.model.gateway.gsm.GsmGateway;
import com.zekiloni.george.platform.domain.model.gateway.gsm.GsmPort;
import com.zekiloni.george.platform.domain.model.gateway.gsm.GsmProvider;

import java.util.List;

public interface GsmGatewayPort {
    boolean isSupported(GsmProvider provider);
    GsmPort getPortStatus(GsmGateway gateway, String port);
    List<GsmPort> getAllPortsStatus(GsmGateway gateway);

    void sendSms(GsmGateway gateway, String port, String phoneNumber, String message);
}
