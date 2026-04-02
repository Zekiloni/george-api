# Form Configuration System - Implementation Guide

## Quick Start

This guide helps you get started with the Form Configuration system in the George API application.

## Entity Overview

The system consists of the following main entities:

1. **FormConfig** - Main form container
2. **FormField** - Base field class with inheritance for specialized types
3. **PasswordField** - Secure password input
4. **AddressField** - Address with multi-part support
5. **PhoneField** - International phone number
6. **CreditCardField** - Payment card input
7. **RatingField** - Star/emoji rating input
8. **RepeatField** - Repeating field groups (arrays)
9. **FieldValidator** - Validation rules
10. **FieldOption** - Selectable options
11. **FormSubmission** - Form submission records

## Database Schema

### Main Tables

```sql
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
);

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
);

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
);

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
);

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
);

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
);

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
);

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
);

-- Field Validators Table
CREATE TABLE field_validators (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    form_field_id BIGINT,
    type VARCHAR(50) NOT NULL,
    validator_value VARCHAR(255),
    error_message TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (form_field_id) REFERENCES form_fields(id) ON DELETE CASCADE
);

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
);

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
);

-- Create indexes
CREATE INDEX idx_form_name ON form_configs(form_name);
CREATE INDEX idx_form_active_public ON form_configs(is_active, is_public);
CREATE INDEX idx_form_field_type ON form_fields(type);
CREATE INDEX idx_form_field_config ON form_fields(form_config_id);
CREATE INDEX idx_form_field_parent ON form_fields(parent_field_id);
CREATE INDEX idx_submission_form ON form_submissions(form_config_id);
CREATE INDEX idx_submission_status ON form_submissions(status);
CREATE INDEX idx_submission_date ON form_submissions(submitted_at);
```

## Services Architecture

Recommended service layer structure:

```
Service Layer:
├── FormConfigService - CRUD operations for forms
├── FormFieldService - Field management
├── FormSubmissionService - Submission handling
├── ValidationService - Field validation logic
├── FormBuilderService - High-level form building
└── FormMapperService - Entity ↔ DTO conversion
```

## Usage Examples

### Creating a Contact Form

```java
// In your service layer
FormConfig form = new FormConfig();
form.setFormName("contact_form");
form.setFormTitle("Contact Us");
form.setDescription("Send us your inquiries");
form.setIsPublic(true);
form.setSuccessMessage("Thank you for contacting us!");
form.setSubmissionEmail("admin@example.com");

// Add name field
FormField nameField = new FormField();
nameField.setFieldName("name");
nameField.setLabel("Full Name");
nameField.setType(FieldType.TEXT);
nameField.setIsRequired(true);
nameField.setDisplayOrder(0);

form.getFields().add(nameField);
formConfigRepository.save(form);
```

### Creating a Payment Form

```java
FormConfig paymentForm = new FormConfig();
paymentForm.setFormName("payment_form");
paymentForm.setFormTitle("Payment Information");

// Add address field
AddressField addressField = new AddressField();
addressField.setFieldName("billing_address");
addressField.setLabel("Billing Address");
addressField.setFormatMultipart(true);
addressField.setIncludeApartment(true);
addressField.setDisplayOrder(0);

// Add credit card field
CreditCardField cardField = new CreditCardField();
cardField.setFieldName("payment_card");
cardField.setLabel("Card Information");
cardField.setAllowedCardTypes("VISA,MASTERCARD");
cardField.setTokenizeCardData(true);
cardField.setDisplayOrder(1);

paymentForm.getFields().add(addressField);
paymentForm.getFields().add(cardField);
formConfigRepository.save(paymentForm);
```

### Creating a Survey with Repeating Items

```java
FormConfig surveyForm = new FormConfig();
surveyForm.setFormName("survey_form");
surveyForm.setFormTitle("Customer Satisfaction Survey");

// Create repeat field for survey items
RepeatField itemsRepeat = new RepeatField();
itemsRepeat.setFieldName("survey_items");
itemsRepeat.setLabel("Rate your experience:");
itemsRepeat.setMinInstances(3);
itemsRepeat.setMaxInstances(10);
itemsRepeat.setDisplayOrder(0);

// Add sub-field (rating)
RatingField ratingField = new RatingField();
ratingField.setFieldName("item_rating");
ratingField.setLabel("Rating");
ratingField.setType(FieldType.RATING);
ratingField.setRatingType("STAR");
ratingField.setMaxRating(5);
ratingField.setDisplayOrder(0);

itemsRepeat.getSubFields().add(ratingField);
surveyForm.getFields().add(itemsRepeat);
formConfigRepository.save(surveyForm);
```

## API Endpoints (Recommended)

```
POST   /api/v1/forms           - Create a new form
GET    /api/v1/forms           - List all public forms
GET    /api/v1/forms/{id}      - Get form details
PUT    /api/v1/forms/{id}      - Update form
DELETE /api/v1/forms/{id}      - Delete form

POST   /api/v1/forms/{id}/fields        - Add field to form
PUT    /api/v1/forms/{id}/fields/{fid}  - Update field
DELETE /api/v1/forms/{id}/fields/{fid}  - Delete field

POST   /api/v1/forms/{id}/submit        - Submit form data
GET    /api/v1/forms/{id}/submissions   - Get submissions
GET    /api/v1/submissions/{sid}        - Get submission details
```

## Best Practices

1. **Always use DTOs** for API communication
2. **Validate fields** before saving to database
3. **Soft-delete support** - Use `isActive` flag instead of hard deletes
4. **Cascade operations** - Deleting a form deletes all its fields
5. **Audit trail** - Track `createdBy` and `updatedBy` for compliance
6. **Lazy loading** - Use lazy loading for collections to improve performance
7. **Security** - Store tokenized credit card data only
8. **Validation** - Run field validators on submission

## Future Enhancements

1. Conditional field visibility
2. Calculated fields
3. Multi-language support
4. Form templates library
5. Analytics and reporting
6. A/B testing variants
7. Webhook integrations
8. File upload handling
9. Payment gateway integration
10. Form versioning


