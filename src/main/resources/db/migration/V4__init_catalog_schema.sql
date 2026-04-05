-- Billing Configs table
CREATE TABLE billing_configs (
    id BINARY(16) PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    quantity_allowed BOOLEAN NOT NULL,
    max_quantity INT,
    duration_allowed BOOLEAN NOT NULL,
    duration_unit VARCHAR(50),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Offerings table
CREATE TABLE offerings (
    id BINARY(16) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description LONGTEXT,
    identifier VARCHAR(255) NOT NULL UNIQUE,
    type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    billing_config_id BINARY(16),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    FOREIGN KEY (billing_config_id) REFERENCES billing_configs(id)
);

-- Offering Prices table
CREATE TABLE offering_prices (
    id BINARY(16) PRIMARY KEY,
    offering_id BINARY(16),
    label VARCHAR(255) NOT NULL,
    unit_price_amount DECIMAL(19, 2) NOT NULL,
    unit_price_currency VARCHAR(3) NOT NULL,
    duration INT,
    duration_unit VARCHAR(50),
    discount DECIMAL(5, 2),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    FOREIGN KEY (offering_id) REFERENCES offerings(id)
);

-- Offering Characteristics table
CREATE TABLE offering_characteristics (
    id BINARY(16) PRIMARY KEY,
    offering_id BINARY(16),
    key VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description LONGTEXT,
    value LONGTEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    FOREIGN KEY (offering_id) REFERENCES offerings(id)
);

-- Indexes
CREATE INDEX idx_offerings_identifier ON offerings(identifier);
CREATE INDEX idx_offerings_status ON offerings(status);
CREATE INDEX idx_offerings_type ON offerings(type);
CREATE INDEX idx_offering_prices_offering_id ON offering_prices(offering_id);
CREATE INDEX idx_offering_characteristics_offering_id ON offering_characteristics(offering_id);

