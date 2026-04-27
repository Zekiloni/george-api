package com.zekiloni.george.platform.application.usecase.campaign;

import com.zekiloni.george.platform.application.port.out.CampaignDispatcherPort;
import com.zekiloni.george.platform.application.port.out.OutreachRepositoryPort;
import com.zekiloni.george.platform.domain.model.campaign.outreach.Outreach;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CampaignDispatchService implements CampaignDispatcherPort {
    private final OutreachRepositoryPort repository;

    @Async("asyncTaskExecutor")
    @Override
    public void dispatch(String campaignId) {
       process(campaignId);
    }

    private void process(String campaignId) {
        Stream<Outreach> byCampaignId = repository.findByCampaignId(campaignId);
    }
}
