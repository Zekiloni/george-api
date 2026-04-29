package com.zekiloni.george.platform.application.port.out.gateway;

import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.platform.domain.model.campaign.outreach.Outreach;
import com.zekiloni.george.platform.domain.model.gateway.Gateway;
import com.zekiloni.george.platform.domain.model.gateway.GatewayType;

import java.util.List;

public interface GatewayDispatchPort<T extends Gateway> {
    boolean isSupported(GatewayType gatewayType);

    void send(List<Outreach> outreach, T gateway);

    default void send(List<Outreach> outreach, T gateway, ServiceAccess serviceAccess) {
        // Default implementation ignores serviceAccess and uses gateway directly
        send(outreach, gateway);
    }
}
