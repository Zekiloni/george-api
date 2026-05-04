package com.zekiloni.george.platform.domain.model.campaign.outreach;

public enum OutreachStatus {
    SCHEDULED,
    /** Handed off to the gateway successfully (SMTP 250, SMS submitted, etc.). */
    SENT,
    /** Confirmed accepted by the final recipient (DSN success / SMS DLR ok). */
    DELIVERED,
    /** Recipient interacted (clicked tracking pixel / link, opened landing page). */
    VISITED,
    /** Hard or soft delivery failure reported by the upstream MTA / provider. */
    BOUNCED,
    /** Recipient marked as spam (ARF/FBL feedback) — treat as a stronger BOUNCED. */
    COMPLAINED,
    /** Failed before/at the gateway (auth, network, validation). */
    FAILED,
    COMPLETED,
    ABANDONED,
}
