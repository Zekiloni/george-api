package com.zekiloni.george.platform.infrastructure.in.web.dto.campaign;

public record CampaignStepAnalyticsDto(
        int stepIndex,
        String pageId,
        String pageName,
        long entered,
        long exited,
        double dropPct,
        Long avgTimeOnStepMs,
        Long p50TimeOnStepMs,
        Long p95TimeOnStepMs
) {}
