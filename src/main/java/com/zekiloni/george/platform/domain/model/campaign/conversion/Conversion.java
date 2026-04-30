package com.zekiloni.george.platform.domain.model.campaign.conversion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Conversion {
    private String id;
    private String tenantId;
    private String sessionId;
    private String outreachId;
    private String campaignId;
    private String pageId;
    private Map<String, Object> formData;
    private String ipAddress;
    private String userAgent;
    private String fingerprint;
    private OffsetDateTime convertedAt;
}
