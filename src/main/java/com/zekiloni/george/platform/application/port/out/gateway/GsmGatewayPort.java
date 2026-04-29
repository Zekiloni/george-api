package com.zekiloni.george.platform.application.port.out.gateway;

import com.zekiloni.george.platform.domain.model.gateway.gsm.GsmGateway;
import com.zekiloni.george.platform.domain.model.gateway.gsm.GsmProvider;

import java.math.BigDecimal;
import java.util.List;

public interface GsmGatewayPort {
    boolean isSupported(GsmProvider provider);
    PortStatus getPortStatus(GsmGateway gateway, String port);
    List<PortStatus> getAllPortsStatus(GsmGateway gateway);

    void sendSms(GsmGateway gateway, String port, String phoneNumber, String message);

    record PortStatus(
            String port,            // "1.01", "2.01"
            boolean active,         // active=1
            boolean inserted,       // sim inserted=1
            boolean slotActive,     // slot_active=1
            int signalStrength,     // sig=29
            String operator,        // "46001 CHINA UNICOM GSM"
            String imsi,            // "460014978..."
            String iccid,           // "8986011628..."
            BigDecimal balance      // bal=100
    ) {}
}
