package com.zekiloni.george.platform.application.port.in.campaign;

import com.zekiloni.george.platform.domain.model.campaign.Campaign;

import java.io.InputStream;

public interface CampaignCreateUseCase {
    Campaign handle(Campaign command, InputStream file);
}
