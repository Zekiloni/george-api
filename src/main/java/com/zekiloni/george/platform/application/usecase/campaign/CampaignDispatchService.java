package com.zekiloni.george.platform.application.usecase.campaign;

import com.zekiloni.george.commerce.application.port.out.InventoryRepositoryPort;
import com.zekiloni.george.platform.application.port.out.campaign.CampaignDispatcherPort;
import com.zekiloni.george.platform.application.port.out.campaign.OutreachRepositoryPort;
import com.zekiloni.george.platform.application.port.out.gateway.GatewayDispatchPort;
import com.zekiloni.george.platform.application.port.out.gateway.GatewayRepositoryPort;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;
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
    private final InventoryRepositoryPort inventoryRepository;
    private final List<GatewayDispatchPort<Gateway>> gatewayDispatcher;

    @Async("asyncTaskExecutor")
    @Override
    public void dispatch(String campaignId, String serviceAccessId) {
        // Get the ServiceAccess to find which gateway to use
        inventoryRepository.findById(serviceAccessId)
                .ifPresent(serviceAccess -> process(serviceAccess, campaignId));
    }

    private void process(ServiceAccess serviceAccess, String campaignId) {
        // Get the gateway associated with this ServiceAccess
        String gatewayId = serviceAccess.getGatewayId();
        gatewayRepository.findById(gatewayId)
                .ifPresent(gateway -> {
                    Stream<Outreach> byCampaignId = repository.findByCampaignId(campaignId);
                    processWithGateway(gateway, serviceAccess, byCampaignId.toList());
                });
    }

    private void processWithGateway(Gateway gateway, ServiceAccess serviceAccess, List<Outreach> outreaches) {
        gatewayDispatcher
                .stream()
                .filter(dispatcher -> dispatcher.isSupported(gateway.getType()))
                .findFirst()
                .ifPresent(dispatcher -> {
                    // Pass both gateway and serviceAccess to the dispatcher
                    dispatcher.send(outreaches, gateway, serviceAccess);
                });
    }
}
