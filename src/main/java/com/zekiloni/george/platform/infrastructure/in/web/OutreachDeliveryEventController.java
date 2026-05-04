package com.zekiloni.george.platform.infrastructure.in.web;

import com.zekiloni.george.platform.application.usecase.campaign.OutreachDeliveryEventService;
import com.zekiloni.george.platform.application.usecase.campaign.OutreachDeliveryEventService.EventType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Generic delivery-event webhook. Any provider (Stalwart bounce poller,
 * Postal, EJOIN DLR, future SMS providers) POSTs delivery outcomes here
 * and we update the corresponding {@code Outreach} idempotently.
 *
 * <p>Auth is a single shared bearer token configured via
 * {@code app.outreach.delivery-webhook-secret}. Keep it secret — anyone
 * with this token can mark any outreach as delivered/bounced.
 */
@RestController
@RequestMapping("${api.base.path:/api/v1}/outreach")
@RequiredArgsConstructor
@Slf4j
public class OutreachDeliveryEventController {

    private final OutreachDeliveryEventService deliveryEventService;

    @Value("${app.outreach.delivery-webhook-secret:}")
    private String webhookSecret;

    /**
     * Accepts a single event or a batch (providers like Postal often send arrays).
     * Returns 200 even if individual events reference unknown outreach ids — the
     * underlying service logs and continues, so the provider doesn't replay forever
     * on transient producer-side data drift.
     */
    @PostMapping("/events")
    public ResponseEntity<Void> events(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody @Valid List<DeliveryEventDto> events) {
        if (!isAuthorized(authHeader)) {
            log.warn("Rejected delivery event POST with bad/missing auth");
            return ResponseEntity.status(401).build();
        }
        for (DeliveryEventDto e : events) {
            deliveryEventService.apply(e.outreachId(), e.type(), e.reason(), e.occurredAt());
        }
        return ResponseEntity.ok().build();
    }

    private boolean isAuthorized(String authHeader) {
        if (webhookSecret == null || webhookSecret.isBlank()) {
            // Permit when not configured so dev environments don't block.
            log.warn("app.outreach.delivery-webhook-secret is not configured — accepting any caller (dev only)");
            return true;
        }
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return false;
        return webhookSecret.equals(authHeader.substring("Bearer ".length()).trim());
    }

    public record DeliveryEventDto(
            @NotBlank String outreachId,
            @NotNull EventType type,
            String reason,
            OffsetDateTime occurredAt
    ) {}
}
