package com.zekiloni.george.platform.application.port.out.campaign;

import com.zekiloni.george.platform.domain.model.campaign.conversion.Conversion;

import java.util.List;

public interface ConversionRepositoryPort {

    Conversion save(Conversion conversion);

    List<Conversion> findByCampaignId(String campaignId);
}
