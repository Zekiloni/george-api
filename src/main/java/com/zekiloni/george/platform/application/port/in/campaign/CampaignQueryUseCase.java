package com.zekiloni.george.platform.application.port.in.campaign;

import com.zekiloni.george.platform.domain.model.campaign.Campaign;
import com.zekiloni.george.platform.domain.model.campaign.CampaignFieldAnalytics;
import com.zekiloni.george.platform.domain.model.campaign.CampaignReferrer;
import com.zekiloni.george.platform.domain.model.campaign.CampaignResponse;
import com.zekiloni.george.platform.domain.model.campaign.CampaignStats;
import com.zekiloni.george.platform.domain.model.campaign.CampaignStepAnalytics;
import com.zekiloni.george.platform.domain.model.campaign.CampaignTimelinePoint;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSession;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSessionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CampaignQueryUseCase {
    Optional<Campaign> findById(String id);
    Page<Campaign> findAll(Pageable pageable);
    CampaignStats stats(String id);
    Page<UserSession> findSessions(String id, Collection<UserSessionStatus> statuses, Pageable pageable);
    List<CampaignResponse> responses(String campaignId);

    List<CampaignStepAnalytics> stepAnalytics(String campaignId);
    List<CampaignFieldAnalytics> fieldAnalytics(String campaignId);
    List<CampaignTimelinePoint> timeline(String campaignId, ChronoUnit bucket);
    List<CampaignReferrer> referrers(String campaignId);
}
