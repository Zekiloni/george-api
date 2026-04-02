package com.zekiloni.george.domain.lead;

/**
 * Enum representing the status of a lead.
 * Tracks the progression of a lead through the campaign.
 */
public enum LeadStatus {
    NEW,                // Lead just joined the campaign
    INVITED,            // Lead has been invited
    ENGAGED,            // Lead has interacted with the campaign
    SUBMITTED,          // Lead has submitted the form
    CONVERTED,          // Lead has completed the desired action
    BOUNCED,            // Lead bounced (e.g., invalid phone number)
    BLOCKED,            // Lead is blocked
    UNSUBSCRIBED;       // Lead has unsubscribed

    public boolean isActive() {
        return this != BOUNCED && this != BLOCKED && this != UNSUBSCRIBED;
    }

    public boolean isConvertible() {
        return this == NEW || this == INVITED || this == ENGAGED;
    }
}

