package com.zekiloni.george.platform.application.usecase.campaign;

import com.zekiloni.george.commerce.application.port.out.InventoryRepositoryPort;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.platform.application.port.out.campaign.OutreachRepositoryPort;
import com.zekiloni.george.platform.application.port.out.gateway.GatewayDispatchPort;
import com.zekiloni.george.platform.application.port.out.gateway.GatewayRepositoryPort;
import com.zekiloni.george.platform.domain.model.campaign.outreach.Outreach;
import com.zekiloni.george.platform.domain.model.gateway.Gateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampaignDispatchProcessor {
    private final OutreachRepositoryPort repository;
    private final GatewayRepositoryPort gatewayRepository;
    private final InventoryRepositoryPort inventoryRepository;
    private final List<GatewayDispatchPort<Gateway>> gatewayDispatcher;

    @Transactional
    public void process(String serviceAccessId, String campaignId) {
        inventoryRepository.findById(serviceAccessId)
                .ifPresent(serviceAccess -> dispatchWithGateway(serviceAccess, campaignId));
    }

    private void dispatchWithGateway(ServiceAccess serviceAccess, String campaignId) {
        String gatewayId = serviceAccess.getGatewayId();
        gatewayRepository.findById(gatewayId)
                .ifPresent(gateway -> {
                    List<Outreach> outreaches = repository.findByCampaignId(campaignId).toList();
                    sendThroughGateway(gateway, serviceAccess, outreaches);
                    repository.saveAll(outreaches);
                });
    }

    private void sendThroughGateway(Gateway gateway, ServiceAccess serviceAccess, List<Outreach> outreaches) {
        int maxAttempts = 3;
        long delayMs = 2000;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                gatewayDispatcher
                        .stream()
                        .filter(dispatcher -> dispatcher.isSupported(gateway.getType()))
                        .findFirst()
                        .ifPresentOrElse(
                                dispatcher -> dispatcher.send(outreaches, gateway, serviceAccess),
                                () -> log.warn("No dispatcher found for gateway type {}", gateway.getType())
                        );
                return;
            } catch (Exception e) {
                if (attempt == maxAttempts) {
                    log.error("Gateway dispatch failed after {} attempts for campaign {}: {}",
                            maxAttempts, serviceAccess.getId(), e.getMessage(), e);
                    throw e;
                }
                log.warn("Gateway dispatch attempt {}/{} failed for campaign {}: {}. Retrying in {}ms",
                        attempt, maxAttempts, serviceAccess.getId(), e.getMessage(), delayMs);
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Dispatch interrupted", ie);
                }
                delayMs *= 2;
            }
        }
    }
}
