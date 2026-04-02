-- V2__init_gsm_box_config_schema.sql
-- Schema for GSM Box Configuration system

-- GSM Box Configuration Table
CREATE TABLE gsm_box_configs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    box_name VARCHAR(255) NOT NULL UNIQUE,
    box_label VARCHAR(255) NOT NULL,
    description TEXT,
    box_type VARCHAR(50) NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    port_number INT DEFAULT 9000,
    imei VARCHAR(15) UNIQUE,
    imsi VARCHAR(15),
    phone_number VARCHAR(20),
    manufacturer VARCHAR(255),
    model VARCHAR(255),
    firmware_version VARCHAR(50),
    status VARCHAR(50) NOT NULL DEFAULT 'OFFLINE',
    signal_strength INT,
    battery_level INT,
    is_active BOOLEAN DEFAULT TRUE,
    is_locked BOOLEAN DEFAULT FALSE,
    allow_sms BOOLEAN DEFAULT TRUE,
    allow_calls BOOLEAN DEFAULT TRUE,
    allow_data BOOLEAN DEFAULT TRUE,
    max_concurrent_calls INT DEFAULT 1,
    max_sms_per_minute INT DEFAULT 10,
    notes TEXT,
    last_seen_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- GSM Protocol Configuration Table
CREATE TABLE gsm_protocol_configs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    gsm_box_config_id BIGINT NOT NULL,
    protocol VARCHAR(50) NOT NULL,
    is_enabled BOOLEAN DEFAULT TRUE,
    priority INT DEFAULT 0,
    port_number INT,
    settings TEXT,
    max_bandwidth BIGINT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (gsm_box_config_id) REFERENCES gsm_box_configs(id) ON DELETE CASCADE,
    UNIQUE KEY uk_box_protocol (gsm_box_config_id, protocol)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- GSM Authentication Credentials Table
CREATE TABLE gsm_auth_credentials (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    gsm_box_config_id BIGINT NOT NULL,
    auth_type VARCHAR(50) NOT NULL,
    credential_name VARCHAR(255) NOT NULL,
    description TEXT,
    credential_value TEXT NOT NULL,
    credential_secret TEXT,
    additional_data TEXT,
    is_primary BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    expires_at TIMESTAMP NULL,
    last_used_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    FOREIGN KEY (gsm_box_config_id) REFERENCES gsm_box_configs(id) ON DELETE CASCADE,
    UNIQUE KEY uk_box_credential (gsm_box_config_id, credential_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- GSM Box Activity Log Table
CREATE TABLE gsm_box_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    gsm_box_config_id BIGINT NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    event_message TEXT,
    error_details TEXT,
    signal_strength INT,
    battery_level INT,
    related_phone VARCHAR(20),
    related_data TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (gsm_box_config_id) REFERENCES gsm_box_configs(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create Indexes for Performance
CREATE INDEX idx_gsm_box_name ON gsm_box_configs(box_name);
CREATE INDEX idx_gsm_box_status ON gsm_box_configs(status);
CREATE INDEX idx_gsm_box_type ON gsm_box_configs(box_type);
CREATE INDEX idx_gsm_box_imei ON gsm_box_configs(imei);
CREATE INDEX idx_gsm_box_phone ON gsm_box_configs(phone_number);
CREATE INDEX idx_gsm_box_ip ON gsm_box_configs(ip_address);
CREATE INDEX idx_gsm_box_active ON gsm_box_configs(is_active);

CREATE INDEX idx_gsm_protocol_box ON gsm_protocol_configs(gsm_box_config_id);
CREATE INDEX idx_gsm_protocol_enabled ON gsm_protocol_configs(gsm_box_config_id, is_enabled);

CREATE INDEX idx_gsm_auth_box ON gsm_auth_credentials(gsm_box_config_id);
CREATE INDEX idx_gsm_auth_active ON gsm_auth_credentials(gsm_box_config_id, is_active);
CREATE INDEX idx_gsm_auth_primary ON gsm_auth_credentials(gsm_box_config_id, is_primary);

CREATE INDEX idx_gsm_log_box ON gsm_box_logs(gsm_box_config_id);
CREATE INDEX idx_gsm_log_event ON gsm_box_logs(gsm_box_config_id, event_type);
CREATE INDEX idx_gsm_log_created ON gsm_box_logs(gsm_box_config_id, created_at);

