package com.zekiloni.george.platform.domain.model.campaign.outreach.session;

import com.zekiloni.george.platform.domain.model.campaign.outreach.Outreach;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSession {
    private String id;
    private String tenantId;
    private String wsToken;
    /** AES-256-GCM key (base64) used by both ends to encrypt payloads on the wire. */
    private String sessionKey;
    private String fingerprint;
    private String userAgent;
    private String ipAddress;
    private List<UserEvent> events;
    private Outreach outreach;
    private UserSessionStatus status;
    private int viewCount;
    private int currentStep;
    private OffsetDateTime lastActivityAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    /** Risk/quality signals computed at session-create time by the simulator: 'vpn', 'proxy', 'tor', 'datacenter', 'bot', 'replay', 'geo_mismatch', 'velocity', 'headless'. */
    private List<String> flags;
    /** ISO-3166-1 alpha-2 of the IP's resolved country. */
    private String country;
    private String city;
    /** Autonomous System Number of the IP — useful to classify hosting/VPN traffic. */
    private Integer asn;
    private String asnOrg;
    /** External IP-reputation provider score (e.g. IPQS 0–100). Null when no provider is configured. */
    private Integer riskScore;
}
