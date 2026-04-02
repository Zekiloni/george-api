-- Flyway Migration: V3__init_campaign_lead_tracking_link_schema.sql
-- Creates campaign, lead, and tracking_link tables

-- Create campaigns table
CREATE TABLE campaigns (
    id BIGSERIAL PRIMARY KEY,
    owner_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    message_template TEXT,
    form_config_id BIGINT,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMP,
    ended_at TIMESTAMP,
    CONSTRAINT fk_campaigns_form_config FOREIGN KEY (form_config_id)
        REFERENCES form_configs(id) ON DELETE SET NULL
);

CREATE INDEX idx_campaigns_owner_id ON campaigns(owner_id);
CREATE INDEX idx_campaigns_status ON campaigns(status);
CREATE INDEX idx_campaigns_created_at ON campaigns(created_at DESC);

-- Create leads table
CREATE TABLE leads (
    id BIGSERIAL PRIMARY KEY,
    campaign_id BIGINT NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    email VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'NEW',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_engaged_at TIMESTAMP,
    submitted_at TIMESTAMP,
    converted_at TIMESTAMP,
    metadata TEXT,
    CONSTRAINT fk_leads_campaign FOREIGN KEY (campaign_id)
        REFERENCES campaigns(id) ON DELETE CASCADE,
    CONSTRAINT uk_leads_campaign_phone UNIQUE(campaign_id, phone_number)
);

CREATE INDEX idx_leads_phone_number ON leads(phone_number);
CREATE INDEX idx_leads_campaign_id ON leads(campaign_id);
CREATE INDEX idx_leads_status ON leads(status);
CREATE INDEX idx_leads_created_at ON leads(created_at DESC);

-- Create tracking_links table
CREATE TABLE tracking_links (
    id BIGSERIAL PRIMARY KEY,
    campaign_id BIGINT NOT NULL,
    lead_id BIGINT NOT NULL,
    token VARCHAR(128) NOT NULL UNIQUE,
    short_url VARCHAR(255) NOT NULL,
    full_url TEXT NOT NULL,
    click_count BIGINT NOT NULL DEFAULT 0,
    last_clicked_at TIMESTAMP,
    form_submission_id BIGINT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_clicked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    metadata TEXT,
    CONSTRAINT fk_tracking_links_campaign FOREIGN KEY (campaign_id)
        REFERENCES campaigns(id) ON DELETE CASCADE,
    CONSTRAINT fk_tracking_links_lead FOREIGN KEY (lead_id)
        REFERENCES leads(id) ON DELETE CASCADE,
    CONSTRAINT fk_tracking_links_form_submission FOREIGN KEY (form_submission_id)
        REFERENCES form_submissions(id) ON DELETE SET NULL
);

CREATE INDEX idx_tracking_links_token ON tracking_links(token);
CREATE INDEX idx_tracking_links_lead_id ON tracking_links(lead_id);
CREATE INDEX idx_tracking_links_campaign_id ON tracking_links(campaign_id);
CREATE INDEX idx_tracking_links_is_clicked ON tracking_links(is_clicked);
CREATE INDEX idx_tracking_links_created_at ON tracking_links(created_at DESC);

