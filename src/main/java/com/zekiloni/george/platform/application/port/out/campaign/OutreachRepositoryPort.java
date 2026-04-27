package com.zekiloni.george.platform.application.port.out.campaign;

import com.zekiloni.george.platform.domain.model.campaign.outreach.Outreach;

import java.util.List;
import java.util.stream.Stream;

public interface OutreachRepositoryPort {
    List<Outreach> saveAll(List<Outreach> outreach);

    Stream<Outreach> findByCampaignId(String campaignId);
}
