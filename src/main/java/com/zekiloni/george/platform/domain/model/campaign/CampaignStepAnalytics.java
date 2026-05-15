package com.zekiloni.george.platform.domain.model.campaign;

public record CampaignStepAnalytics(
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
