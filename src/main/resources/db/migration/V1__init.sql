CREATE TABLE attachments
(
    id          UUID         NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255),
    description VARCHAR(255),
    file_name   VARCHAR(255) NOT NULL,
    mime_type   VARCHAR(255) NOT NULL,
    size        BIGINT       NOT NULL,
    href        VARCHAR(255),
    type        VARCHAR(255),
    valid_from  TIMESTAMP WITHOUT TIME ZONE,
    valid_to    TIMESTAMP WITHOUT TIME ZONE,
    is_primary  BOOLEAN,
    CONSTRAINT pk_attachments PRIMARY KEY (id)
);

CREATE TABLE campaigns
(
    id                UUID         NOT NULL,
    tenant_id         VARCHAR(255),
    created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255),
    name              VARCHAR(255) NOT NULL,
    token_strategy    VARCHAR(255),
    token_length      INTEGER      NOT NULL,
    message_template  VARCHAR(255),
    status            VARCHAR(255) NOT NULL,
    base_url          VARCHAR(255) NOT NULL,
    flow_page_ids     JSONB,
    service_access_id UUID         NOT NULL,
    scheduled_at      TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_campaigns PRIMARY KEY (id)
);

CREATE TABLE characteristic_specifications
(
    id              UUID         NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    name            VARCHAR(255) NOT NULL,
    description     VARCHAR(255),
    value_type      VARCHAR(255),
    configurable    BOOLEAN,
    extensible      BOOLEAN,
    is_unique       BOOLEAN,
    max_cardinality INTEGER,
    min_cardinality INTEGER,
    regex           VARCHAR(255),
    price_impact    VARCHAR(255) NOT NULL,
    offering_id     UUID,
    start_date_time TIMESTAMP WITH TIME ZONE,
    end_date_time   TIMESTAMP WITH TIME ZONE,
    CONSTRAINT pk_characteristic_specifications PRIMARY KEY (id)
);

CREATE TABLE characteristic_value_specifications
(
    id                              UUID NOT NULL,
    created_at                      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at                      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by                      VARCHAR(255),
    updated_by                      VARCHAR(255),
    is_default                      BOOLEAN,
    range_interval                  VARCHAR(255),
    regex                           VARCHAR(255),
    unit_of_measure                 VARCHAR(255),
    value_from                      VARCHAR(255),
    value_to                        VARCHAR(255),
    value_type                      VARCHAR(255),
    value                           JSONB,
    start_date_time                 TIMESTAMP WITHOUT TIME ZONE,
    end_date_time                   TIMESTAMP WITHOUT TIME ZONE,
    price_adjustment_amount         NUMERIC(19, 2),
    price_adjustment_currency       VARCHAR(3),
    characteristic_specification_id UUID,
    CONSTRAINT pk_characteristic_value_specifications PRIMARY KEY (id)
);

CREATE TABLE characteristics
(
    id         UUID         NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    name       VARCHAR(255) NOT NULL,
    value      JSONB        NOT NULL,
    CONSTRAINT pk_characteristics PRIMARY KEY (id)
);

CREATE TABLE conversions
(
    id           UUID         NOT NULL,
    tenant_id    VARCHAR(255),
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by   VARCHAR(255),
    updated_by   VARCHAR(255),
    session_id   VARCHAR(255) NOT NULL,
    outreach_id  VARCHAR(255),
    campaign_id  VARCHAR(255),
    page_id      VARCHAR(255),
    form_data    JSONB,
    ip_address   VARCHAR(255),
    user_agent   VARCHAR(255),
    fingerprint  VARCHAR(255),
    converted_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_conversions PRIMARY KEY (id)
);

CREATE TABLE coupons
(
    id                      UUID         NOT NULL,
    created_at              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by              VARCHAR(255),
    updated_by              VARCHAR(255),
    code                    VARCHAR(255) NOT NULL,
    name                    VARCHAR(255),
    description             TEXT,
    type                    VARCHAR(255) NOT NULL,
    duration                VARCHAR(255) NOT NULL,
    percent                 DECIMAL(5, 4),
    duration_in_periods     INTEGER,
    applies_to_offering_ids JSONB,
    valid_from              TIMESTAMP WITHOUT TIME ZONE,
    valid_to                TIMESTAMP WITHOUT TIME ZONE,
    max_redemptions         INTEGER,
    redeemed_count          INTEGER      NOT NULL,
    status                  VARCHAR(255) NOT NULL,
    fixed_amount            NUMERIC(19, 2),
    fixed_currency          VARCHAR(3),
    min_order_amount        NUMERIC(19, 2),
    min_order_currency      VARCHAR(3),
    CONSTRAINT pk_coupons PRIMARY KEY (id)
);

CREATE TABLE discount_tiers
(
    id          UUID          NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255),
    from_units  INTEGER       NOT NULL,
    discount    DECIMAL(5, 4) NOT NULL,
    offering_id UUID,
    CONSTRAINT pk_discount_tiers PRIMARY KEY (id)
);

CREATE TABLE gateways
(
    id          UUID         NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255),
    type        VARCHAR(255) NOT NULL,
    name        VARCHAR(255),
    description VARCHAR(1000),
    enabled     BOOLEAN,
    priority    INTEGER,
    config      JSONB,
    CONSTRAINT pk_gateways PRIMARY KEY (id)
);

CREATE TABLE gsm_gateways
(
    id       UUID         NOT NULL,
    provider VARCHAR(255) NOT NULL,
    CONSTRAINT pk_gsm_gateways PRIMARY KEY (id)
);

CREATE TABLE gsm_service_access
(
    id   UUID NOT NULL,
    port INTEGER,
    CONSTRAINT pk_gsm_service_access PRIMARY KEY (id)
);

CREATE TABLE invoice_items
(
    id          UUID    NOT NULL,
    tenant_id   VARCHAR(255),
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255),
    offering_id    UUID    NOT NULL,
    units          INTEGER NOT NULL,
    invoice_id     UUID    NOT NULL,
    total_amount   NUMERIC(19, 2) NOT NULL,
    total_currency VARCHAR(3)     NOT NULL,
    CONSTRAINT pk_invoice_items PRIMARY KEY (id)
);

CREATE TABLE invoices
(
    id                UUID         NOT NULL,
    tenant_id         VARCHAR(255),
    created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255),
    invoice_number    VARCHAR(255) NOT NULL,
    status            VARCHAR(255) NOT NULL,
    invoice_type      VARCHAR(255) NOT NULL,
    service_access_id VARCHAR(255),
    payment_reference VARCHAR(255),
    issued_at         TIMESTAMP WITHOUT TIME ZONE,
    due_at            TIMESTAMP WITHOUT TIME ZONE,
    paid_at           TIMESTAMP WITHOUT TIME ZONE,
    cancelled_at      TIMESTAMP WITHOUT TIME ZONE,
    refunded_at       TIMESTAMP WITHOUT TIME ZONE,
    note              VARCHAR(255),
    order_id          UUID,
    CONSTRAINT pk_invoices PRIMARY KEY (id)
);

CREATE TABLE lead_service_access
(
    id UUID NOT NULL,
    CONSTRAINT pk_lead_service_access PRIMARY KEY (id)
);

CREATE TABLE lead_service_access_leads
(
    lead_id           UUID NOT NULL,
    service_access_id UUID NOT NULL,
    CONSTRAINT pk_lead_service_access_leads PRIMARY KEY (lead_id, service_access_id)
);

CREATE TABLE leads
(
    id           UUID         NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by   VARCHAR(255),
    updated_by   VARCHAR(255),
    country      VARCHAR(255),
    area_code    VARCHAR(255),
    region_code  VARCHAR(255),
    carrier      VARCHAR(255),
    location     VARCHAR(255),
    phone_number VARCHAR(255) NOT NULL,
    CONSTRAINT pk_leads PRIMARY KEY (id)
);

CREATE TABLE offerings
(
    id                    UUID         NOT NULL,
    created_at            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by            VARCHAR(255),
    updated_by            VARCHAR(255),
    name                  VARCHAR(255) NOT NULL,
    description           TEXT,
    identifier            VARCHAR(255) NOT NULL,
    status                VARCHAR(255) NOT NULL,
    service_specification VARCHAR(255) NOT NULL,
    unit_label            VARCHAR(255),
    time_unit             VARCHAR(255),
    interval_count        INTEGER      NOT NULL,
    min_units             INTEGER,
    max_units             INTEGER,
    tier_mode             VARCHAR(255) NOT NULL,
    renewal_notice_days   INTEGER,
    grace_period_days     INTEGER,
    unit_amount           NUMERIC(19, 2) NOT NULL,
    unit_currency         VARCHAR(3)     NOT NULL,
    CONSTRAINT pk_offerings PRIMARY KEY (id)
);

CREATE TABLE order_item_characteristics
(
    characteristic_id UUID NOT NULL,
    order_item_id     UUID NOT NULL
);

CREATE TABLE order_items
(
    id          UUID    NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255),
    offering_id UUID    NOT NULL,
    units       INTEGER NOT NULL,
    order_id    UUID,
    CONSTRAINT pk_order_items PRIMARY KEY (id)
);

CREATE TABLE orders
(
    id          UUID         NOT NULL,
    tenant_id   VARCHAR(255),
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255),
    status      VARCHAR(255) NOT NULL,
    coupon_code VARCHAR(255),
    CONSTRAINT pk_orders PRIMARY KEY (id)
);

CREATE TABLE outreach
(
    id             UUID         NOT NULL,
    tenant_id      VARCHAR(255),
    created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by     VARCHAR(255),
    updated_by     VARCHAR(255),
    session_token  VARCHAR(255) NOT NULL,
    recipient      VARCHAR(255) NOT NULL,
    message        VARCHAR(255) NOT NULL,
    status         VARCHAR(255) NOT NULL,
    external_id    VARCHAR(255),
    country        VARCHAR(255),
    carrier        VARCHAR(255),
    campaign_id    UUID         NOT NULL,
    scheduled_at   TIMESTAMP WITHOUT TIME ZONE,
    dispatched_at  TIMESTAMP WITHOUT TIME ZONE,
    delivered_at   TIMESTAMP WITHOUT TIME ZONE,
    failed_at      TIMESTAMP WITHOUT TIME ZONE,
    bounced_at     TIMESTAMP WITHOUT TIME ZONE,
    complained_at  TIMESTAMP WITHOUT TIME ZONE,
    failure_reason VARCHAR(500),
    CONSTRAINT pk_outreach PRIMARY KEY (id)
);

CREATE TABLE page_service_access
(
    id             UUID NOT NULL,
    max_concurrent INTEGER,
    CONSTRAINT pk_page_service_access PRIMARY KEY (id)
);

CREATE TABLE page_service_access_pages
(
    page_access_id UUID NOT NULL,
    page_id        UUID NOT NULL,
    CONSTRAINT pk_page_service_access_pages PRIMARY KEY (page_access_id, page_id)
);

CREATE TABLE page_templates
(
    id          UUID         NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255),
    title       VARCHAR(255) NOT NULL,
    slug        VARCHAR(255),
    description TEXT,
    keywords    TEXT,
    favicon_url TEXT,
    source      VARCHAR(255) NOT NULL,
    definition  JSONB        NOT NULL,
    manifest    JSONB,
    CONSTRAINT pk_page_templates PRIMARY KEY (id)
);

CREATE TABLE pages
(
    id          UUID         NOT NULL,
    tenant_id   VARCHAR(255),
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255),
    title       VARCHAR(255) NOT NULL,
    slug        VARCHAR(255) NOT NULL,
    description TEXT,
    keywords    TEXT,
    favicon_url TEXT,
    status      VARCHAR(255) NOT NULL,
    definition  JSONB        NOT NULL,
    CONSTRAINT pk_pages PRIMARY KEY (id)
);

CREATE TABLE service_access
(
    id                    UUID         NOT NULL,
    tenant_id             VARCHAR(255),
    created_at            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by            VARCHAR(255),
    updated_by            VARCHAR(255),
    service_specification VARCHAR(255) NOT NULL,
    valid_from            TIMESTAMP WITHOUT TIME ZONE,
    valid_to              TIMESTAMP WITHOUT TIME ZONE,
    status                VARCHAR(255) NOT NULL,
    suspended_at          TIMESTAMP WITHOUT TIME ZONE,
    terminated_at         TIMESTAMP WITHOUT TIME ZONE,
    renewal_count         INTEGER      NOT NULL,
    cancel_at_period_end  BOOLEAN      NOT NULL,
    order_item_id         UUID         NOT NULL,
    gateway_id            VARCHAR(255),
    CONSTRAINT pk_service_access PRIMARY KEY (id)
);

CREATE TABLE service_access_characteristics
(
    characteristic_id UUID NOT NULL,
    service_access_id UUID NOT NULL
);

CREATE TABLE smtp_gateways
(
    id       UUID         NOT NULL,
    provider VARCHAR(255) NOT NULL,
    CONSTRAINT pk_smtp_gateways PRIMARY KEY (id)
);

CREATE TABLE smtp_service_access
(
    id          UUID NOT NULL,
    smtp_server VARCHAR(255),
    port        INTEGER,
    username    VARCHAR(255),
    password    VARCHAR(255),
    CONSTRAINT pk_smtp_service_access PRIMARY KEY (id)
);

CREATE TABLE user_sessions
(
    id               UUID         NOT NULL,
    tenant_id        VARCHAR(255),
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255),
    ws_token         VARCHAR(255),
    fingerprint      VARCHAR(255),
    user_agent       VARCHAR(255),
    ip_address       VARCHAR(255),
    outreach_id      UUID         NOT NULL,
    status           VARCHAR(255) NOT NULL,
    view_count       INTEGER      NOT NULL,
    last_activity_at TIMESTAMP WITHOUT TIME ZONE,
    events           JSONB,
    CONSTRAINT pk_user_sessions PRIMARY KEY (id)
);

ALTER TABLE page_templates
    ADD CONSTRAINT uc_4591f94b4373abc70db41a47d UNIQUE (title, source);

ALTER TABLE coupons
    ADD CONSTRAINT uc_coupons_code UNIQUE (code);

ALTER TABLE invoices
    ADD CONSTRAINT uc_invoices_invoice_number UNIQUE (invoice_number);

ALTER TABLE invoices
    ADD CONSTRAINT uc_invoices_order UNIQUE (order_id);

ALTER TABLE leads
    ADD CONSTRAINT uc_leads_phone_number UNIQUE (phone_number);

ALTER TABLE offerings
    ADD CONSTRAINT uc_offerings_identifier UNIQUE (identifier);

ALTER TABLE pages
    ADD CONSTRAINT uc_pages_slug UNIQUE (slug);

ALTER TABLE service_access
    ADD CONSTRAINT uc_service_access_order_item UNIQUE (order_item_id);

ALTER TABLE user_sessions
    ADD CONSTRAINT uc_user_sessions_ws_token UNIQUE (ws_token);

ALTER TABLE characteristic_value_specifications
    ADD CONSTRAINT FK_CHARACTERISTICVALUESPECIFICAT_ON_CHARACTERISTICSPECIFICATION FOREIGN KEY (characteristic_specification_id) REFERENCES characteristic_specifications (id);

ALTER TABLE characteristic_specifications
    ADD CONSTRAINT FK_CHARACTERISTIC_SPECIFICATIONS_ON_OFFERING FOREIGN KEY (offering_id) REFERENCES offerings (id);

ALTER TABLE discount_tiers
    ADD CONSTRAINT FK_DISCOUNT_TIERS_ON_OFFERING FOREIGN KEY (offering_id) REFERENCES offerings (id);

ALTER TABLE gsm_gateways
    ADD CONSTRAINT FK_GSM_GATEWAYS_ON_ID FOREIGN KEY (id) REFERENCES gateways (id);

ALTER TABLE gsm_service_access
    ADD CONSTRAINT FK_GSM_SERVICE_ACCESS_ON_ID FOREIGN KEY (id) REFERENCES service_access (id);

ALTER TABLE invoices
    ADD CONSTRAINT FK_INVOICES_ON_ORDER FOREIGN KEY (order_id) REFERENCES orders (id);

ALTER TABLE invoice_items
    ADD CONSTRAINT FK_INVOICE_ITEMS_ON_INVOICE FOREIGN KEY (invoice_id) REFERENCES invoices (id);

ALTER TABLE invoice_items
    ADD CONSTRAINT FK_INVOICE_ITEMS_ON_OFFERING FOREIGN KEY (offering_id) REFERENCES offerings (id);

ALTER TABLE lead_service_access
    ADD CONSTRAINT FK_LEAD_SERVICE_ACCESS_ON_ID FOREIGN KEY (id) REFERENCES service_access (id);

ALTER TABLE order_items
    ADD CONSTRAINT FK_ORDER_ITEMS_ON_OFFERING FOREIGN KEY (offering_id) REFERENCES offerings (id);

ALTER TABLE order_items
    ADD CONSTRAINT FK_ORDER_ITEMS_ON_ORDER FOREIGN KEY (order_id) REFERENCES orders (id);

ALTER TABLE outreach
    ADD CONSTRAINT FK_OUTREACH_ON_CAMPAIGN FOREIGN KEY (campaign_id) REFERENCES campaigns (id);

ALTER TABLE page_service_access
    ADD CONSTRAINT FK_PAGE_SERVICE_ACCESS_ON_ID FOREIGN KEY (id) REFERENCES service_access (id);

ALTER TABLE service_access
    ADD CONSTRAINT FK_SERVICE_ACCESS_ON_ORDER_ITEM FOREIGN KEY (order_item_id) REFERENCES order_items (id);

ALTER TABLE smtp_gateways
    ADD CONSTRAINT FK_SMTP_GATEWAYS_ON_ID FOREIGN KEY (id) REFERENCES gateways (id);

ALTER TABLE smtp_service_access
    ADD CONSTRAINT FK_SMTP_SERVICE_ACCESS_ON_ID FOREIGN KEY (id) REFERENCES service_access (id);

ALTER TABLE user_sessions
    ADD CONSTRAINT FK_USER_SESSIONS_ON_OUTREACH FOREIGN KEY (outreach_id) REFERENCES outreach (id);

ALTER TABLE lead_service_access_leads
    ADD CONSTRAINT fk_leaseracclea_on_lead_entity FOREIGN KEY (lead_id) REFERENCES leads (id);

ALTER TABLE lead_service_access_leads
    ADD CONSTRAINT fk_leaseracclea_on_lead_service_access_entity FOREIGN KEY (service_access_id) REFERENCES lead_service_access (id);

ALTER TABLE order_item_characteristics
    ADD CONSTRAINT fk_orditecha_on_characteristic_entity FOREIGN KEY (characteristic_id) REFERENCES characteristics (id);

ALTER TABLE order_item_characteristics
    ADD CONSTRAINT fk_orditecha_on_order_item_entity FOREIGN KEY (order_item_id) REFERENCES order_items (id);

ALTER TABLE page_service_access_pages
    ADD CONSTRAINT fk_pagseraccpag_on_page_entity FOREIGN KEY (page_id) REFERENCES pages (id);

ALTER TABLE page_service_access_pages
    ADD CONSTRAINT fk_pagseraccpag_on_page_service_access_entity FOREIGN KEY (page_access_id) REFERENCES page_service_access (id);

ALTER TABLE service_access_characteristics
    ADD CONSTRAINT fk_seracccha_on_characteristic_entity FOREIGN KEY (characteristic_id) REFERENCES characteristics (id);

ALTER TABLE service_access_characteristics
    ADD CONSTRAINT fk_seracccha_on_service_access_entity FOREIGN KEY (service_access_id) REFERENCES service_access (id);