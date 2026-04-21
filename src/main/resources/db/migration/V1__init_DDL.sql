CREATE TABLE billing_configs
(
    id               UUID                        NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255),
    type             VARCHAR(255)                NOT NULL,
    quantity_allowed BOOLEAN                     NOT NULL,
    max_quantity     INTEGER,
    duration_allowed BOOLEAN                     NOT NULL,
    duration_unit    VARCHAR(255),
    CONSTRAINT pk_billing_configs PRIMARY KEY (id)
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
    offering_id     UUID,
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

CREATE TABLE invoice_items
(
    id              UUID    NOT NULL,
    tenant_id       VARCHAR(255),
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    offering_id     UUID    NOT NULL,
    quantity        INTEGER NOT NULL,
    discount_amount DECIMAL,
    invoice_id      UUID    NOT NULL,
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
    payment_reference VARCHAR(255),
    issued_at         TIMESTAMP WITHOUT TIME ZONE,
    due_at            TIMESTAMP WITHOUT TIME ZONE,
    paid_at           TIMESTAMP WITHOUT TIME ZONE,
    cancelled_at      TIMESTAMP WITHOUT TIME ZONE,
    refunded_at       TIMESTAMP WITHOUT TIME ZONE,
    note              VARCHAR(255),
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

CREATE TABLE offering_prices
(
    id            UUID         NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by    VARCHAR(255),
    updated_by    VARCHAR(255),
    label         VARCHAR(255) NOT NULL,
    duration      INTEGER,
    duration_unit VARCHAR(255),
    discount      DECIMAL,
    offering_id   UUID,
    CONSTRAINT pk_offering_prices PRIMARY KEY (id)
);

CREATE TABLE offerings
(
    id                       UUID         NOT NULL,
    created_at               TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at               TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by               VARCHAR(255),
    updated_by               VARCHAR(255),
    name                     VARCHAR(255) NOT NULL,
    description              TEXT,
    identifier               VARCHAR(255) NOT NULL,
    status                   VARCHAR(255) NOT NULL,
    service_specification_id VARCHAR(255) NOT NULL,
    billing_config_id        UUID,
    CONSTRAINT pk_offerings PRIMARY KEY (id)
);

CREATE TABLE order_item_characteristics
(
    characteristic_id UUID NOT NULL,
    order_item_id     UUID NOT NULL
);

CREATE TABLE order_items
(
    id            UUID NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by    VARCHAR(255),
    updated_by    VARCHAR(255),
    offering_id   UUID NOT NULL,
    quantity      INTEGER,
    duration      INTEGER,
    duration_unit VARCHAR(255),
    invoice_id    UUID,
    order_id      UUID,
    CONSTRAINT pk_order_items PRIMARY KEY (id)
);

CREATE TABLE orders
(
    id         UUID         NOT NULL,
    tenant_id  VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    status     VARCHAR(255) NOT NULL,
    invoice_id UUID,
    CONSTRAINT pk_orders PRIMARY KEY (id)
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
    keywords    VARCHAR(255),
    favicon_url VARCHAR(255),
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
    order_item_id         UUID         NOT NULL,
    CONSTRAINT pk_service_access PRIMARY KEY (id)
);

CREATE TABLE service_access_characteristics
(
    characteristic_id UUID NOT NULL,
    service_access_id UUID NOT NULL
);

CREATE TABLE smtp_service_access
(
    id          UUID    NOT NULL,
    smtp_server VARCHAR(255),
    port        INTEGER NOT NULL,
    username    VARCHAR(255),
    password    VARCHAR(255),
    CONSTRAINT pk_smtp_service_access PRIMARY KEY (id)
);

CREATE TABLE tracking_events
(
    id                  UUID         NOT NULL,
    created_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255),
    tracking_link_id    UUID         NOT NULL,
    event_type          VARCHAR(255) NOT NULL,
    ip_address          VARCHAR(255),
    user_agent          VARCHAR(255),
    referer             VARCHAR(255),
    event_timestamp     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    metadata            TEXT,
    session_id          VARCHAR(255),
    device_type         VARCHAR(255),
    geographic_location VARCHAR(255),
    CONSTRAINT pk_tracking_events PRIMARY KEY (id)
);

CREATE TABLE tracking_links
(
    id              UUID         NOT NULL,
    tenant_id       VARCHAR(255),
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    token           VARCHAR(255) NOT NULL,
    original_url    VARCHAR(255) NOT NULL,
    short_code      VARCHAR(255),
    is_active       BOOLEAN      NOT NULL,
    click_count     INTEGER,
    last_clicked_at TIMESTAMP WITHOUT TIME ZONE,
    expires_at      TIMESTAMP WITHOUT TIME ZONE,
    metadata        TEXT,
    CONSTRAINT pk_tracking_links PRIMARY KEY (id)
);

ALTER TABLE invoices
    ADD CONSTRAINT uc_invoices_invoice_number UNIQUE (invoice_number);

ALTER TABLE leads
    ADD CONSTRAINT uc_leads_phone_number UNIQUE (phone_number);

ALTER TABLE offerings
    ADD CONSTRAINT uc_offerings_billing_config UNIQUE (billing_config_id);

ALTER TABLE offerings
    ADD CONSTRAINT uc_offerings_identifier UNIQUE (identifier);

ALTER TABLE order_items
    ADD CONSTRAINT uc_order_items_invoice UNIQUE (invoice_id);

ALTER TABLE orders
    ADD CONSTRAINT uc_orders_invoice UNIQUE (invoice_id);

ALTER TABLE pages
    ADD CONSTRAINT uc_pages_slug UNIQUE (slug);

ALTER TABLE service_access
    ADD CONSTRAINT uc_service_access_order_item UNIQUE (order_item_id);

ALTER TABLE tracking_links
    ADD CONSTRAINT uc_tracking_links_short_code UNIQUE (short_code);

ALTER TABLE tracking_links
    ADD CONSTRAINT uc_tracking_links_token UNIQUE (token);

ALTER TABLE characteristic_value_specifications
    ADD CONSTRAINT FK_CHARACTERISTICVALUESPECIFICAT_ON_CHARACTERISTICSPECIFICATION FOREIGN KEY (characteristic_specification_id) REFERENCES characteristic_specifications (id);

ALTER TABLE characteristic_specifications
    ADD CONSTRAINT FK_CHARACTERISTIC_SPECIFICATIONS_ON_OFFERING FOREIGN KEY (offering_id) REFERENCES offerings (id);

ALTER TABLE invoice_items
    ADD CONSTRAINT FK_INVOICE_ITEMS_ON_INVOICE FOREIGN KEY (invoice_id) REFERENCES invoices (id);

ALTER TABLE invoice_items
    ADD CONSTRAINT FK_INVOICE_ITEMS_ON_OFFERING FOREIGN KEY (offering_id) REFERENCES offerings (id);

ALTER TABLE lead_service_access
    ADD CONSTRAINT FK_LEAD_SERVICE_ACCESS_ON_ID FOREIGN KEY (id) REFERENCES service_access (id);

ALTER TABLE offerings
    ADD CONSTRAINT FK_OFFERINGS_ON_BILLING_CONFIG FOREIGN KEY (billing_config_id) REFERENCES billing_configs (id);

ALTER TABLE offering_prices
    ADD CONSTRAINT FK_OFFERING_PRICES_ON_OFFERING FOREIGN KEY (offering_id) REFERENCES offerings (id);

ALTER TABLE orders
    ADD CONSTRAINT FK_ORDERS_ON_INVOICE FOREIGN KEY (invoice_id) REFERENCES invoices (id);

ALTER TABLE order_items
    ADD CONSTRAINT FK_ORDER_ITEMS_ON_INVOICE FOREIGN KEY (invoice_id) REFERENCES invoices (id);

ALTER TABLE order_items
    ADD CONSTRAINT FK_ORDER_ITEMS_ON_OFFERING FOREIGN KEY (offering_id) REFERENCES offerings (id);

ALTER TABLE order_items
    ADD CONSTRAINT FK_ORDER_ITEMS_ON_ORDER FOREIGN KEY (order_id) REFERENCES orders (id);

ALTER TABLE page_service_access
    ADD CONSTRAINT FK_PAGE_SERVICE_ACCESS_ON_ID FOREIGN KEY (id) REFERENCES service_access (id);

ALTER TABLE service_access
    ADD CONSTRAINT FK_SERVICE_ACCESS_ON_ORDER_ITEM FOREIGN KEY (order_item_id) REFERENCES order_items (id);

ALTER TABLE smtp_service_access
    ADD CONSTRAINT FK_SMTP_SERVICE_ACCESS_ON_ID FOREIGN KEY (id) REFERENCES service_access (id);

ALTER TABLE tracking_events
    ADD CONSTRAINT FK_TRACKING_EVENTS_ON_TRACKING_LINK FOREIGN KEY (tracking_link_id) REFERENCES tracking_links (id);

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