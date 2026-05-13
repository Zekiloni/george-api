package com.zekiloni.george.platform.application.port.in.template;

import com.zekiloni.george.platform.domain.model.campaign.Campaign;

import java.util.Map;

public interface CreateCampaignFromTemplateUseCase {

    /**
     * Clones the given template's flow into tenant-owned Pages after
     * substituting any user-supplied variable values, then assembles a fresh
     * draft Campaign with those Pages as its flow. Increments the template's
     * usage counter as a side effect.
     *
     * @param templateId    the source template
     * @param campaignName  human-readable name for the new campaign
     * @param variables     map of variable name → value the user supplied;
     *                      missing values fall back to each variable's
     *                      declared default
     * @return the saved campaign
     */
    Campaign handle(String templateId, String campaignName, Map<String, String> variables);
}
