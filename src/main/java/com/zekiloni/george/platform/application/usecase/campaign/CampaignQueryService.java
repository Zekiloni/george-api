package com.zekiloni.george.platform.application.usecase.campaign;

import com.zekiloni.george.platform.application.port.in.campaign.CampaignQueryUseCase;
import com.zekiloni.george.platform.application.port.out.campaign.CampaignRepositoryPort;
import com.zekiloni.george.platform.application.port.out.campaign.OutreachRepositoryPort;
import com.zekiloni.george.platform.application.port.out.campaign.UserSessionRepositoryPort;
import com.zekiloni.george.platform.domain.model.campaign.Campaign;
import com.zekiloni.george.platform.domain.model.campaign.CampaignResponse;
import com.zekiloni.george.platform.domain.model.campaign.CampaignStats;
import com.zekiloni.george.platform.domain.model.campaign.outreach.Outreach;
import com.zekiloni.george.platform.domain.model.campaign.outreach.OutreachStatus;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.InteractionType;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserEvent;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSession;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSessionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
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

    @Override
    public List<CampaignResponse> responses(String campaignId) {
        // TODO: paginate — v1 caps server-side at RESPONSES_CAP rows.
        List<UserSession> sessions = sessionRepository.findCompletedByCampaignId(campaignId, RESPONSES_CAP);
        return sessions.stream().map(CampaignQueryService::toResponse).toList();
    }

    private static final int RESPONSES_CAP = 500;

    private static CampaignResponse toResponse(UserSession session) {
        Outreach outreach = session.getOutreach();
        return new CampaignResponse(
                session.getId(),
                outreach != null ? outreach.getId() : null,
                outreach != null ? outreach.getRecipient() : null,
                session.getStatus(),
                session.getCurrentStep(),
                session.getIpAddress(),
                session.getUserAgent(),
                // markCompleted bumps updatedAt — use it as the completion timestamp
                // until we add a dedicated completedAt column.
                session.getStatus() == UserSessionStatus.COMPLETED ? session.getUpdatedAt() : null,
                session.getCreatedAt(),
                mergeSubmitPayloads(session.getEvents())
        );
    }

    /**
     * Merge every SUBMIT event's payload into a single Map. Later submits
     * overwrite earlier values on key collision — a re-submit on a later
     * step wins. INPUT_CHANGE and other event types are ignored because
     * their payloads may still be opaque AES-GCM envelopes (only SUBMIT
     * payloads were normalized to plaintext at write time).
     */
    private static Map<String, Object> mergeSubmitPayloads(List<UserEvent> events) {
        Map<String, Object> merged = new LinkedHashMap<>();
        if (events == null) {
            return merged;
        }
        for (UserEvent event : events) {
            if (event.getType() == InteractionType.SUBMIT && event.getPayload() != null) {
                merged.putAll(event.getPayload());
            }
        }
        return merged;
    }

    private static long countAny(Map<OutreachStatus, Long> counts, OutreachStatus... statuses) {
        long sum = 0;
        for (OutreachStatus s : statuses) sum += counts.getOrDefault(s, 0L);
        return sum;
    }
}
