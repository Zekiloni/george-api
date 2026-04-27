package com.zekiloni.george.platform.application.port.out;

import com.zekiloni.george.platform.domain.model.campaign.outreach.Outreach;
import com.zekiloni.george.platform.domain.model.gatway.Gateway;
import com.zekiloni.george.platform.domain.model.gatway.GatewayType;

import java.util.List;

public interface GatewayDispatchPort<T extends Gateway> {
    boolean isSupported(GatewayType gatewayType);
    void send(List<Outreach> outreach, T gateway);
}
