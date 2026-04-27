package com.zekiloni.george.platform.application.usecase.campaign;

import com.zekiloni.george.platform.application.port.out.campaign.CampaignDispatcherPort;
import com.zekiloni.george.platform.application.port.out.campaign.OutreachRepositoryPort;
import com.zekiloni.george.platform.application.port.out.gateway.GatewayDispatchPort;
import com.zekiloni.george.platform.application.port.out.gateway.GatewayRepositoryPort;
import com.zekiloni.george.platform.domain.model.campaign.outreach.Outreach;
import com.zekiloni.george.platform.domain.model.gatway.Gateway;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CampaignDispatchService implements CampaignDispatcherPort {
    private final OutreachRepositoryPort repository;
    private final GatewayRepositoryPort gatewayRepository;
    private final List<GatewayDispatchPort<Gateway>> gatewayDispatcher;

    @Async("asyncTaskExecutor")
    @Override
    public void dispatch(String campaignId, String gatewayId) {
        gatewayRepository.findById(gatewayId)
                .ifPresent(gateway -> process(gateway, campaignId));
    }

    private void process(Gateway gateway, String campaignId) {
        Stream<Outreach> byCampaignId = repository.findByCampaignId(campaignId);
        gatewayDispatcher
                .stream()
                .filter(dispatcher -> dispatcher.isSupported(gateway.getType()))
                .findFirst()
                .ifPresent(dispatcher -> dispatcher.send(byCampaignId.toList(), gateway));

    }
}
