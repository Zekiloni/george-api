package com.zekiloni.george.platform.application.port.in.campaign;

import com.zekiloni.george.platform.domain.model.campaign.Campaign;

import java.util.List;

public interface CampaignUpdateUseCase {
    Campaign pause(String campaignId);
    Campaign resume(String campaignId);
    Campaign abort(String campaignId);
    Campaign complete(String campaignId);

    /**
     * Replace the campaign's geo block list with the given alpha-2 codes.
     * Pass an empty list to clear the block. Codes are normalized to upper
     * case server-side; the gate compares case-insensitively anyway.
     */
    Campaign updateBlockedCountries(String campaignId, List<String> blockedCountries);
}
