-- Audit trail for every campaign status transition (DRAFT → SCHEDULED,
-- SCHEDULED → ACTIVE, ACTIVE → PAUSED, etc.). Records who did it and when.

CREATE TABLE campaign_status_transitions (
    id          UUID         NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255),
    campaign_id VARCHAR(255) NOT NULL,
    from_status VARCHAR(255) NOT NULL,
    to_status   VARCHAR(255) NOT NULL,
    occurred_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_campaign_status_transitions PRIMARY KEY (id)
);

CREATE INDEX idx_cst_campaign_id ON campaign_status_transitions (campaign_id);
