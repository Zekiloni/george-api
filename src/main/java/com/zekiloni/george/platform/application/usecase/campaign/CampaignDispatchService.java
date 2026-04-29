package com.zekiloni.george.platform.application.usecase.campaign;

import com.zekiloni.george.commerce.application.port.out.InventoryRepositoryPort;
import com.zekiloni.george.platform.application.port.out.campaign.CampaignDispatcherPort;
import com.zekiloni.george.platform.application.port.out.campaign.OutreachRepositoryPort;
import com.zekiloni.george.platform.application.port.out.gateway.GatewayDispatchPort;
import com.zekiloni.george.platform.application.port.out.gateway.GatewayRepositoryPort;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.platform.domain.model.campaign.outreach.Outreach;
import com.zekiloni.george.platform.domain.model.gateway.Gateway;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        inventoryRepository.findById(serviceAccessId)
                .ifPresent(serviceAccess -> process(serviceAccess, campaignId));
    }

    @Transactional
    public void process(ServiceAccess serviceAccess, String campaignId) {
        String gatewayId = serviceAccess.getGatewayId();
        gatewayRepository.findById(gatewayId)
                .ifPresent(gateway -> {
                    List<Outreach> outreaches = repository.findByCampaignId(campaignId).toList();
                    processWithGateway(gateway, serviceAccess, outreaches);
                    repository.saveAll(outreaches);
                });
    }

    private void processWithGateway(Gateway gateway, ServiceAccess serviceAccess, List<Outreach> outreaches) {
        gatewayDispatcher
                .stream()
                .filter(dispatcher -> dispatcher.isSupported(gateway.getType()))
                .findFirst()
                .ifPresent(dispatcher -> dispatcher.send(outreaches, gateway, serviceAccess));
    }
}
