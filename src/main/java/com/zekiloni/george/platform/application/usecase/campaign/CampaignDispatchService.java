package com.zekiloni.george.platform.application.usecase.campaign;

import com.zekiloni.george.platform.application.port.out.campaign.CampaignDispatcherPort;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CampaignDispatchService implements CampaignDispatcherPort {
    private final CampaignDispatchProcessor processor;

    @Async("asyncTaskExecutor")
    @Override
    public void dispatch(String campaignId, String serviceAccessId) {
        processor.process(serviceAccessId, campaignId);
    }
}
