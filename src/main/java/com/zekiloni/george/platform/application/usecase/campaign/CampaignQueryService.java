package com.zekiloni.george.platform.application.usecase.campaign;

import com.zekiloni.george.platform.application.port.in.campaign.CampaignQueryUseCase;
import com.zekiloni.george.platform.application.port.out.campaign.CampaignRepositoryPort;
import com.zekiloni.george.platform.application.port.out.campaign.OutreachRepositoryPort;
import com.zekiloni.george.platform.application.port.out.campaign.UserSessionRepositoryPort;
import com.zekiloni.george.platform.domain.model.campaign.Campaign;
import com.zekiloni.george.platform.domain.model.campaign.CampaignStats;
import com.zekiloni.george.platform.domain.model.campaign.outreach.OutreachStatus;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSession;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSessionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CampaignQueryService implements CampaignQueryUseCase {
    private final CampaignRepositoryPort campaignRepository;
    private final OutreachRepositoryPort outreachRepository;
    private final UserSessionRepositoryPort sessionRepository;

    @Override
    public Optional<Campaign> findById(String id) {
        return campaignRepository.findById(id);
    }

    @Override
    public Page<Campaign> findAll(Pageable pageable) {
        return campaignRepository.findAll(pageable);
    }

    @Override
    public CampaignStats stats(String id) {
        Map<OutreachStatus, Long> outreach = outreachRepository.countByCampaignGroupedByStatus(id);
        Map<UserSessionStatus, Long> sessions = sessionRepository.countByCampaignGroupedByStatus(id);

        long total = outreach.values().stream().mapToLong(Long::longValue).sum();
        long sent = countAny(outreach, OutreachStatus.SENT, OutreachStatus.VISITED, OutreachStatus.COMPLETED, OutreachStatus.ABANDONED);
        long visited = countAny(outreach, OutreachStatus.VISITED, OutreachStatus.COMPLETED, OutreachStatus.ABANDONED);
        long completedOutreach = outreach.getOrDefault(OutreachStatus.COMPLETED, 0L);
        long completedSessions = sessions.getOrDefault(UserSessionStatus.COMPLETED, 0L);

        return new CampaignStats(
                total,
                sent,
                visited,
                completedOutreach,
                outreach.getOrDefault(OutreachStatus.ABANDONED, 0L),
                sessions.getOrDefault(UserSessionStatus.ACTIVE, 0L),
                sessions.getOrDefault(UserSessionStatus.IDLE, 0L),
                completedSessions,
                sessions.getOrDefault(UserSessionStatus.ABANDONED, 0L),
                sessions.getOrDefault(UserSessionStatus.BLOCKED, 0L),
                sent == 0 ? 0d : ((double) completedSessions) / sent
        );
    }

    @Override
    public Page<UserSession> findSessions(String id, Collection<UserSessionStatus> statuses, Pageable pageable) {
        Collection<UserSessionStatus> effective = (statuses == null || statuses.isEmpty()) ? null : statuses;
        return sessionRepository.findByCampaignId(id, effective, pageable);
    }

    private static long countAny(Map<OutreachStatus, Long> counts, OutreachStatus... statuses) {
        long sum = 0;
        for (OutreachStatus s : statuses) sum += counts.getOrDefault(s, 0L);
        return sum;
    }
}
