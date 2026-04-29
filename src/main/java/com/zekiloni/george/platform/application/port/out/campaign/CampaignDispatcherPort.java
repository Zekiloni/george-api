package com.zekiloni.george.platform.application.port.out.campaign;

public interface CampaignDispatcherPort {
    void dispatch(String campaignId, String serviceAccessId);
}
