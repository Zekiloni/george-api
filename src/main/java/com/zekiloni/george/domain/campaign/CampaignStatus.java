package com.zekiloni.george.domain.campaign;

/**
 * Enum representing the status of a campaign.
 * Tracks the lifecycle of a campaign from creation to completion.
 */
public enum CampaignStatus {
    DRAFT,           // Campaign is still being prepared
    SCHEDULED,       // Campaign is scheduled to launch
    ACTIVE,          // Campaign is currently running
    PAUSED,          // Campaign is paused
    COMPLETED,       // Campaign has finished
    ARCHIVED,        // Campaign is archived
    CANCELLED;       // Campaign was cancelled

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isEditable() {
        return this == DRAFT || this == PAUSED;
    }
}

