package com.zekiloni.george.platform.application.port.out.campaign;

import com.zekiloni.george.platform.domain.model.campaign.outreach.Outreach;

import java.util.Optional;
import java.util.List;
import java.util.stream.Stream;

public interface OutreachRepositoryPort {
    Outreach save(Outreach outreach);
    List<Outreach> saveAll(List<Outreach> outreach);

    Stream<Outreach> findByCampaignId(String campaignId);
    Optional<Outreach> findBySessionToken(String sessionToken);
}
