package com.zekiloni.george.platform.application.usecase.campaign;

import com.zekiloni.george.common.domain.model.Ref;
import com.zekiloni.george.platform.application.port.in.campaign.CampaignQueryUseCase;
import com.zekiloni.george.platform.application.port.out.campaign.CampaignRepositoryPort;
import com.zekiloni.george.platform.application.port.out.campaign.OutreachRepositoryPort;
import com.zekiloni.george.platform.application.port.out.campaign.UserSessionRepositoryPort;
import com.zekiloni.george.platform.domain.model.campaign.Campaign;
import com.zekiloni.george.platform.domain.model.campaign.CampaignFieldAnalytics;
import com.zekiloni.george.platform.domain.model.campaign.CampaignReferrer;
import com.zekiloni.george.platform.domain.model.campaign.CampaignResponse;
import com.zekiloni.george.platform.domain.model.campaign.CampaignStats;
import com.zekiloni.george.platform.domain.model.campaign.CampaignStepAnalytics;
import com.zekiloni.george.platform.domain.model.campaign.CampaignTimelinePoint;
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
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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

    @Override
    @Transactional(readOnly = true)
    public List<CampaignStepAnalytics> stepAnalytics(String campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId).orElseThrow();
        List<Ref> flow = campaign.getFlow() == null ? List.of() : campaign.getFlow();
        if (flow.isEmpty()) {
            return List.of();
        }
        List<UserSession> sessions = sessionRepository.findAllByCampaignId(campaignId);

        // Per-step dwell samples in ms, derived from session.createdAt -> first SUBMIT, then SUBMIT -> SUBMIT.
        // SUBMIT events are the only reliable step-boundary marker today; LOAD events don't carry a step
        // identifier so we can't anchor dwells to specific pages. The samples below assume linear progression
        // through the flow (submit N corresponds to step N). Re-submits on the same step skew the average.
        // TODO: add step markers (e.g. LOAD payload.stepIndex) for higher-fidelity dwell timing.
        List<List<Long>> samplesByStep = new ArrayList<>();
        for (int i = 0; i < flow.size(); i++) {
            samplesByStep.add(new ArrayList<>());
        }

        for (UserSession session : sessions) {
            List<OffsetDateTime> submitTimes = submitTimestamps(session);
            OffsetDateTime previous = session.getCreatedAt();
            for (int i = 0; i < submitTimes.size() && i < flow.size(); i++) {
                OffsetDateTime submit = submitTimes.get(i);
                if (previous != null && submit != null) {
                    long ms = Duration.between(previous, submit).toMillis();
                    if (ms >= 0) {
                        samplesByStep.get(i).add(ms);
                    }
                }
                previous = submit;
            }
        }

        List<CampaignStepAnalytics> out = new ArrayList<>(flow.size());
        for (int i = 0; i < flow.size(); i++) {
            Ref step = flow.get(i);
            int stepIndex = i;
            long entered = sessions.stream().filter(s -> s.getCurrentStep() >= stepIndex).count();
            long exited = sessions.stream().filter(s -> s.getCurrentStep() > stepIndex).count();
            double dropPct = entered == 0 ? 0d : ((double) (entered - exited)) / entered;

            List<Long> samples = samplesByStep.get(i);
            Long avg = samples.isEmpty() ? null : (long) samples.stream().mapToLong(Long::longValue).average().orElse(0d);
            Long p50 = samples.isEmpty() ? null : percentile(samples, 50);
            Long p95 = samples.isEmpty() ? null : percentile(samples, 95);

            out.add(new CampaignStepAnalytics(
                    i,
                    step.getId(),
                    step.getName(),
                    entered,
                    exited,
                    dropPct,
                    avg,
                    p50,
                    p95
            ));
        }
        return out;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CampaignFieldAnalytics> fieldAnalytics(String campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId).orElseThrow();
        List<Ref> flow = campaign.getFlow() == null ? List.of() : campaign.getFlow();
        if (flow.isEmpty()) {
            return List.of();
        }
        List<UserSession> sessions = sessionRepository.findAllByCampaignId(campaignId);

        long[] reachedStep = new long[flow.size()];
        // [stepIndex] -> field name -> filled count
        List<Map<String, Long>> filledByStep = new ArrayList<>();
        // [stepIndex] -> field name -> bucketed value -> count
        List<Map<String, Map<String, Long>>> valuesByStep = new ArrayList<>();
        for (int i = 0; i < flow.size(); i++) {
            filledByStep.add(new HashMap<>());
            valuesByStep.add(new HashMap<>());
        }

        for (UserSession session : sessions) {
            for (int i = 0; i <= session.getCurrentStep() && i < flow.size(); i++) {
                reachedStep[i]++;
            }
            int stepCursor = 0;
            for (UserEvent event : sortedEvents(session)) {
                if (event.getType() != InteractionType.SUBMIT || event.getPayload() == null) {
                    continue;
                }
                if (stepCursor >= flow.size()) {
                    break;
                }
                Map<String, Long> filled = filledByStep.get(stepCursor);
                Map<String, Map<String, Long>> values = valuesByStep.get(stepCursor);
                for (Map.Entry<String, Object> e : event.getPayload().entrySet()) {
                    Object raw = e.getValue();
                    if (raw == null) {
                        continue;
                    }
                    String stringValue = String.valueOf(raw).trim();
                    if (stringValue.isEmpty()) {
                        continue;
                    }
                    filled.merge(e.getKey(), 1L, Long::sum);
                    String bucketed = bucketValue(stringValue);
                    values.computeIfAbsent(e.getKey(), k -> new HashMap<>()).merge(bucketed, 1L, Long::sum);
                }
                stepCursor++;
            }
        }

        List<CampaignFieldAnalytics> out = new ArrayList<>(flow.size());
        for (int i = 0; i < flow.size(); i++) {
            Ref step = flow.get(i);
            long reached = reachedStep[i];
            Map<String, Long> filled = filledByStep.get(i);
            Map<String, Map<String, Long>> values = valuesByStep.get(i);

            List<CampaignFieldAnalytics.Field> fields = new ArrayList<>();
            for (Map.Entry<String, Long> field : filled.entrySet()) {
                double fillRate = reached == 0 ? 0d : ((double) field.getValue()) / reached;
                List<CampaignFieldAnalytics.TopValue> top = values.getOrDefault(field.getKey(), Map.of())
                        .entrySet()
                        .stream()
                        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                        .limit(5)
                        .map(v -> new CampaignFieldAnalytics.TopValue(v.getKey(), v.getValue()))
                        .toList();
                // INPUT_CHANGE timings are too noisy to derive a reliable per-field dwell — return null
                // until we have an event shape that ties focus/blur to a specific field key.
                fields.add(new CampaignFieldAnalytics.Field(field.getKey(), fillRate, null, top));
            }
            fields.sort(Comparator.comparing(CampaignFieldAnalytics.Field::name));
            out.add(new CampaignFieldAnalytics(i, step.getName(), fields));
        }
        return out;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CampaignTimelinePoint> timeline(String campaignId, ChronoUnit bucket) {
        ChronoUnit unit = bucket == null ? ChronoUnit.HOURS : bucket;
        List<UserSession> sessions = sessionRepository.findAllByCampaignId(campaignId);
        List<Outreach> outreach = outreachRepository.findByCampaignId(campaignId).toList();

        OffsetDateTime min = null;
        OffsetDateTime max = OffsetDateTime.now();
        for (Outreach o : outreach) {
            if (o.getDispatchedAt() != null) {
                if (min == null || o.getDispatchedAt().isBefore(min)) min = o.getDispatchedAt();
                if (o.getDispatchedAt().isAfter(max)) max = o.getDispatchedAt();
            }
        }
        for (UserSession s : sessions) {
            if (s.getCreatedAt() != null) {
                if (min == null || s.getCreatedAt().isBefore(min)) min = s.getCreatedAt();
                if (s.getCreatedAt().isAfter(max)) max = s.getCreatedAt();
            }
            if (s.getUpdatedAt() != null && s.getUpdatedAt().isAfter(max)) {
                max = s.getUpdatedAt();
            }
        }
        if (min == null) {
            return List.of();
        }

        // Cap series length: snap to days if the requested resolution would exceed BUCKET_CAP buckets.
        ChronoUnit effectiveUnit = unit;
        long span = effectiveUnit.between(truncate(min, effectiveUnit), truncate(max, effectiveUnit)) + 1;
        if (span > BUCKET_CAP && effectiveUnit == ChronoUnit.HOURS) {
            effectiveUnit = ChronoUnit.DAYS;
            span = effectiveUnit.between(truncate(min, effectiveUnit), truncate(max, effectiveUnit)) + 1;
        }
        if (span > BUCKET_CAP) {
            span = BUCKET_CAP;
        }

        Map<OffsetDateTime, long[]> buckets = new LinkedHashMap<>();
        OffsetDateTime start = truncate(min, effectiveUnit);
        for (long i = 0; i < span; i++) {
            buckets.put(start.plus(i, effectiveUnit), new long[3]);
        }

        for (Outreach o : outreach) {
            if (o.getDispatchedAt() == null) continue;
            OffsetDateTime key = truncate(o.getDispatchedAt(), effectiveUnit);
            long[] cell = buckets.get(key);
            if (cell != null) cell[0]++;
        }
        for (UserSession s : sessions) {
            if (s.getCreatedAt() != null) {
                long[] cell = buckets.get(truncate(s.getCreatedAt(), effectiveUnit));
                if (cell != null) cell[1]++;
            }
            if (s.getStatus() == UserSessionStatus.COMPLETED && s.getUpdatedAt() != null) {
                long[] cell = buckets.get(truncate(s.getUpdatedAt(), effectiveUnit));
                if (cell != null) cell[2]++;
            }
        }

        return buckets.entrySet().stream()
                .map(e -> new CampaignTimelinePoint(e.getKey(), e.getValue()[0], e.getValue()[1], e.getValue()[2]))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CampaignReferrer> referrers(String campaignId) {
        List<UserSession> sessions = sessionRepository.findAllByCampaignId(campaignId);
        Map<String, Long> counts = new HashMap<>();
        for (UserSession session : sessions) {
            String host = firstLoadReferrerHost(session);
            if (host == null) {
                continue;
            }
            counts.merge(host, 1L, Long::sum);
        }
        return counts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(REFERRER_CAP)
                .map(e -> new CampaignReferrer(e.getKey(), e.getValue()))
                .toList();
    }

    private static String firstLoadReferrerHost(UserSession session) {
        if (session.getEvents() == null) return null;
        for (UserEvent event : sortedEvents(session)) {
            if (event.getType() != InteractionType.LOAD) continue;
            Map<String, Object> payload = event.getPayload();
            if (payload == null) return "(direct)";
            Object referrer = payload.get("referrer");
            if (referrer == null || String.valueOf(referrer).isBlank()) {
                return "(direct)";
            }
            try {
                URI uri = URI.create(String.valueOf(referrer).trim());
                String host = uri.getHost();
                if (host == null || host.isBlank()) {
                    return "(direct)";
                }
                return host.toLowerCase(Locale.ROOT);
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }
        return null;
    }

    private static List<OffsetDateTime> submitTimestamps(UserSession session) {
        if (session.getEvents() == null) return List.of();
        return sortedEvents(session).stream()
                .filter(e -> e.getType() == InteractionType.SUBMIT)
                .map(UserEvent::getCreatedAt)
                .toList();
    }

    private static List<UserEvent> sortedEvents(UserSession session) {
        if (session.getEvents() == null) return List.of();
        return session.getEvents().stream()
                .filter(e -> e.getCreatedAt() != null)
                .sorted(Comparator.comparing(UserEvent::getCreatedAt))
                .toList();
    }

    private static long percentile(List<Long> samples, int p) {
        if (samples.isEmpty()) return 0L;
        List<Long> sorted = new ArrayList<>(samples);
        Collections.sort(sorted);
        int idx = (int) Math.ceil((p / 100d) * sorted.size()) - 1;
        if (idx < 0) idx = 0;
        if (idx >= sorted.size()) idx = sorted.size() - 1;
        return sorted.get(idx);
    }

    private static String bucketValue(String raw) {
        if (raw.contains("@")) {
            int at = raw.lastIndexOf('@');
            return raw.substring(at).toLowerCase(Locale.ROOT);
        }
        return raw;
    }

    private static OffsetDateTime truncate(OffsetDateTime t, ChronoUnit unit) {
        OffsetDateTime utc = t.withOffsetSameInstant(ZoneOffset.UTC);
        return switch (unit) {
            case DAYS -> utc.truncatedTo(ChronoUnit.DAYS);
            case HOURS -> utc.truncatedTo(ChronoUnit.HOURS);
            default -> utc.truncatedTo(ChronoUnit.HOURS);
        };
    }

    private static final int RESPONSES_CAP = 500;
    private static final int REFERRER_CAP = 20;
    private static final int BUCKET_CAP = 168;

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
