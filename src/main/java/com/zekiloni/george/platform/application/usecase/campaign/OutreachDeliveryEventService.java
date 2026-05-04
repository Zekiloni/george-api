package com.zekiloni.george.platform.application.usecase.campaign;

import com.zekiloni.george.platform.application.port.out.campaign.OutreachRepositoryPort;
import com.zekiloni.george.platform.domain.model.campaign.outreach.Outreach;
import com.zekiloni.george.platform.domain.model.campaign.outreach.OutreachStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * Idempotent delivery-event sink. Anything (Stalwart bounce poller, Postal
 * webhooks, EJOIN DLR HTTP callbacks, future SMS providers) feeds events
 * into this single service so the {@link Outreach} state stays consistent.
 *
 * <p>State transitions are <i>monotonic</i>: an outreach already DELIVERED
 * stays DELIVERED — a late BOUNCED event is logged and ignored. Same for
 * BOUNCED/COMPLAINED already-set: a later DELIVERED event is rejected as
 * a contradiction (most providers send terminal events at most once, so
 * the second wins-or-loses rule rarely matters in practice).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OutreachDeliveryEventService {
    private final OutreachRepositoryPort outreachRepository;

    public enum EventType { DELIVERED, BOUNCED, COMPLAINED, FAILED }

    @Transactional
    public void apply(String outreachId, EventType type, String reason, OffsetDateTime occurredAt) {
        Optional<Outreach> opt = outreachRepository.findById(outreachId);
        if (opt.isEmpty()) {
            log.warn("Delivery event for unknown outreach {}: {} ({})", outreachId, type, reason);
            return;
        }
        Outreach outreach = opt.get();
        OffsetDateTime ts = occurredAt != null ? occurredAt : OffsetDateTime.now();

        OutreachStatus current = outreach.getStatus();
        if (isTerminal(current) && !overrides(type, current)) {
            log.info("Ignoring {} event for outreach {} already in terminal status {}",
                    type, outreachId, current);
            return;
        }

        switch (type) {
            case DELIVERED -> {
                outreach.setStatus(OutreachStatus.DELIVERED);
                outreach.setDeliveredAt(ts);
            }
            case BOUNCED -> {
                outreach.setStatus(OutreachStatus.BOUNCED);
                outreach.setBouncedAt(ts);
                outreach.setFailureReason(truncate(reason));
            }
            case COMPLAINED -> {
                outreach.setStatus(OutreachStatus.COMPLAINED);
                outreach.setComplainedAt(ts);
                outreach.setFailureReason(truncate(reason));
            }
            case FAILED -> {
                outreach.setStatus(OutreachStatus.FAILED);
                outreach.setFailedAt(ts);
                outreach.setFailureReason(truncate(reason));
            }
        }
        outreachRepository.save(outreach);
        log.info("Outreach {} -> {} (reason={})", outreachId, outreach.getStatus(), reason);
    }

    private static boolean isTerminal(OutreachStatus s) {
        return s == OutreachStatus.DELIVERED
                || s == OutreachStatus.BOUNCED
                || s == OutreachStatus.COMPLAINED
                || s == OutreachStatus.FAILED;
    }

    /**
     * Allow COMPLAINED to override DELIVERED (FBL arrives after the inbox accepted)
     * and BOUNCED to override DELIVERED (rare but happens on async deferrals turning
     * into hard failures). Everything else: first terminal status wins.
     */
    private static boolean overrides(EventType incoming, OutreachStatus current) {
        if (current == OutreachStatus.DELIVERED) {
            return incoming == EventType.COMPLAINED || incoming == EventType.BOUNCED;
        }
        return false;
    }

    private static String truncate(String reason) {
        if (reason == null) return null;
        return reason.length() > 500 ? reason.substring(0, 500) : reason;
    }
}
