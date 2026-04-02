-- V1__init_form_config_schema.sql
-- Initial schema for Form Configuration system

-- Form Configuration Table
CREATE TABLE form_configs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    form_name VARCHAR(255) NOT NULL UNIQUE,
    form_title VARCHAR(255) NOT NULL,
    description TEXT,
    success_message TEXT,
    error_message TEXT,
    redirect_url_on_success VARCHAR(512),
    submission_email VARCHAR(255),
    send_confirmation_email BOOLEAN DEFAULT FALSE,
    notification_webhook_url VARCHAR(512),
    is_active BOOLEAN DEFAULT TRUE,
    is_public BOOLEAN DEFAULT FALSE,
    show_progress_bar BOOLEAN DEFAULT FALSE,
    show_section_numbers BOOLEAN DEFAULT TRUE,
    save_drafts BOOLEAN DEFAULT FALSE,
    enable_captcha BOOLEAN DEFAULT FALSE,
    require_login BOOLEAN DEFAULT FALSE,
    css_customization TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Form Fields Table (JOINED inheritance)
CREATE TABLE form_fields (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    field_type VARCHAR(31) NOT NULL,
    form_config_id BIGINT,
    parent_field_id BIGINT,
    field_name VARCHAR(255) NOT NULL,
    label VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    placeholder TEXT,
    help_text TEXT,
    default_value TEXT,
    display_order INT DEFAULT 0,
    is_required BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    is_read_only BOOLEAN DEFAULT FALSE,
    is_hidden BOOLEAN DEFAULT FALSE,
    custom_attributes TEXT,
    FOREIGN KEY (form_config_id) REFERENCES form_configs(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_field_id) REFERENCES form_fields(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Password Field Table
CREATE TABLE password_fields (
    id BIGINT PRIMARY KEY,
    require_uppercase BOOLEAN DEFAULT FALSE,
    require_lowercase BOOLEAN DEFAULT FALSE,
    require_numbers BOOLEAN DEFAULT FALSE,
    require_special_chars BOOLEAN DEFAULT FALSE,
    min_strength INT DEFAULT 0,
    show_strength_indicator BOOLEAN DEFAULT TRUE,
    allow_show_password BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (id) REFERENCES form_fields(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Address Field Table
CREATE TABLE address_fields (
    id BIGINT PRIMARY KEY,
    include_street BOOLEAN DEFAULT TRUE,
    include_city BOOLEAN DEFAULT TRUE,
    include_state BOOLEAN DEFAULT TRUE,
    include_zip BOOLEAN DEFAULT TRUE,
    include_country BOOLEAN DEFAULT TRUE,
    include_apartment BOOLEAN DEFAULT FALSE,
    format_multipart BOOLEAN DEFAULT TRUE,
    require_country_code VARCHAR(2),
    enable_autocomplete BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (id) REFERENCES form_fields(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Phone Field Table
CREATE TABLE phone_fields (
    id BIGINT PRIMARY KEY,
    default_country_code VARCHAR(2),
    allowed_country_codes VARCHAR(500),
    format_pattern VARCHAR(255),
    include_country_selector BOOLEAN DEFAULT FALSE,
    enable_international_format BOOLEAN DEFAULT FALSE,
    show_country_flag BOOLEAN DEFAULT FALSE,
    validate_actual_number BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (id) REFERENCES form_fields(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Credit Card Field Table
CREATE TABLE credit_card_fields (
    id BIGINT PRIMARY KEY,
    include_card_number BOOLEAN DEFAULT TRUE,
    include_card_holder BOOLEAN DEFAULT TRUE,
    include_expiry BOOLEAN DEFAULT TRUE,
    include_cvv BOOLEAN DEFAULT TRUE,
    include_billing_address BOOLEAN DEFAULT FALSE,
    format_multipart BOOLEAN DEFAULT TRUE,
    allowed_card_types VARCHAR(255),
    show_card_preview BOOLEAN DEFAULT TRUE,
    require_billing_address BOOLEAN DEFAULT FALSE,
    enable_3d_secure BOOLEAN DEFAULT FALSE,
    tokenize_card_data BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (id) REFERENCES form_fields(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Rating Field Table
CREATE TABLE rating_fields (
    id BIGINT PRIMARY KEY,
    rating_type VARCHAR(50),
    max_rating INT DEFAULT 5,
    allow_half_rating BOOLEAN DEFAULT FALSE,
    show_labels BOOLEAN DEFAULT FALSE,
    label_poor VARCHAR(255),
    label_excellent VARCHAR(255),
    icon_size INT DEFAULT 24,
    icon_spacing INT DEFAULT 10,
    FOREIGN KEY (id) REFERENCES form_fields(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Repeat Field Table
CREATE TABLE repeat_fields (
    id BIGINT PRIMARY KEY,
    min_instances INT DEFAULT 1,
    max_instances INT,
    allow_add BOOLEAN DEFAULT TRUE,
    allow_remove BOOLEAN DEFAULT TRUE,
    allow_reorder BOOLEAN DEFAULT FALSE,
    add_button_label VARCHAR(255) DEFAULT 'Add',
    remove_button_label VARCHAR(255) DEFAULT 'Remove',
    show_counter BOOLEAN DEFAULT TRUE,
    preview_fields VARCHAR(500),
    FOREIGN KEY (id) REFERENCES form_fields(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Field Validators Table
CREATE TABLE field_validators (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    form_field_id BIGINT,
    type VARCHAR(50) NOT NULL,
    validator_value VARCHAR(255),
    error_message TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (form_field_id) REFERENCES form_fields(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Field Options Table
CREATE TABLE field_options (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    form_field_id BIGINT,
    label VARCHAR(255) NOT NULL,
    value VARCHAR(255) NOT NULL,
    display_order INT,
    is_default BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    description TEXT,
    FOREIGN KEY (form_field_id) REFERENCES form_fields(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Form Submissions Table
CREATE TABLE form_submissions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    form_config_id BIGINT NOT NULL,
    submission_data LONGTEXT,
    submitted_by VARCHAR(255),
    submitted_at TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent TEXT,
    status VARCHAR(50),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (form_config_id) REFERENCES form_configs(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create indexes for better query performance
CREATE INDEX idx_form_name ON form_configs(form_name);
CREATE INDEX idx_form_active_public ON form_configs(is_active, is_public);
CREATE INDEX idx_form_field_type ON form_fields(type);
CREATE INDEX idx_form_field_config ON form_fields(form_config_id);
CREATE INDEX idx_form_field_parent ON form_fields(parent_field_id);
CREATE INDEX idx_submission_form ON form_submissions(form_config_id);
CREATE INDEX idx_submission_status ON form_submissions(status);
CREATE INDEX idx_submission_date ON form_submissions(submitted_at);
CREATE INDEX idx_validator_field ON field_validators(form_field_id);
CREATE INDEX idx_option_field ON field_options(form_field_id);

