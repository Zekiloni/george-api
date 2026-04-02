# Form Configuration Entity System

This document describes the comprehensive entity system for creating and managing dynamic forms in the George API application.

## Overview

The form configuration system allows users to create complex, multi-field forms with advanced features including:
- Basic input fields (text, email, number, etc.)
- Advanced preset fields (password, address, phone, credit card, etc.)
- Repeating field groups (arrays of fields)
- Field validation rules
- Conditional logic support
- Form submission tracking

## Entity Hierarchy

```
FormConfig (Main form container)
├── FormField (Base field - uses JOINED inheritance)
│   ├── PasswordField
│   ├── AddressField
│   ├── PhoneField
│   ├── CreditCardField
│   ├── RatingField
│   └── RepeatField (contains nested FormFields)
├── FieldValidator (Validation rules)
└── FieldOption (For select/radio fields)

FormSubmission (Tracks form data submissions)
└── references → FormConfig
```

## Core Entities

### 1. FormConfig
Main entity representing a complete form configuration.

**Key Properties:**
- `formName` - Unique identifier for the form
- `formTitle` - Display title
- `description` - Form description
- `isActive` - Enable/disable form
- `isPublic` - Public accessibility
- `sendConfirmationEmail` - Auto-send confirmation
- `fields` - List of form fields (one-to-many relationship)

**Features:**
- Timestamp tracking (createdAt, updatedAt)
- User tracking (createdBy, updatedBy)
- Success/error messages
- Webhook support for notifications
- Custom CSS support
- Progress bar options

### 2. FormField (Base Class)
Abstract base class for all field types using JOINED inheritance strategy.

**Common Properties:**
- `fieldName` - Unique field identifier within the form
- `label` - Display label
- `type` - FieldType enum
- `placeholder` - Input placeholder text
- `helpText` - Helper text/description
- `defaultValue` - Pre-filled value
- `displayOrder` - Field ordering
- `isRequired` - Mandatory field flag
- `isActive` - Enable/disable field
- `isReadOnly` - Read-only flag
- `isHidden` - Hidden field flag
- `validators` - List of validation rules
- `options` - List of selectable options
- `subFields` - Nested fields (for REPEAT type)

### 3. FieldType Enum
Comprehensive enumeration of supported field types.

**Basic Types:**
- TEXT, EMAIL, NUMBER
- CHECKBOX, RADIO, SELECT, MULTI_SELECT
- TEXTAREA, DATE, TIME, DATETIME
- FILE, URL, HIDDEN

**Advanced/Preset Types:**
- PASSWORD
- ADDRESS
- PHONE_NUMBER
- CREDIT_CARD
- ZIP_CODE
- CURRENCY
- PERCENTAGE
- COLOR_PICKER
- RATING
- SIGNATURE
- REPEAT

### 4. FieldValidator
Represents validation rules applied to form fields.

**Properties:**
- `type` - ValidationType enum
- `value` - Validation parameter (e.g., pattern, length)
- `errorMessage` - Custom error message
- `isActive` - Enable/disable validator

**Supported Validations:**
- REQUIRED
- MIN_LENGTH, MAX_LENGTH
- PATTERN (regex)
- MIN_VALUE, MAX_VALUE
- EMAIL, URL, PHONE
- CREDIT_CARD
- DATE_FORMAT
- CUSTOM (extensible)

### 5. FieldOption
Represents selectable options for dropdown/radio/multi-select fields.

**Properties:**
- `label` - Display text
- `value` - Actual value
- `displayOrder` - Sort order
- `isDefault` - Default selection
- `isActive` - Enable/disable option
- `description` - Optional description

## Specialized Field Types

### PasswordField
Extends FormField for secure password input.

**Features:**
- Strength requirements (uppercase, lowercase, numbers, special chars)
- Strength indicator
- Show/hide password toggle
- Minimum strength validation

```java
PasswordField field = new PasswordField();
field.setRequireUppercase(true);
field.setRequireNumbers(true);
field.setMinStrength(2); // good strength
field.setShowStrengthIndicator(true);
```

### AddressField
Extends FormField for address input with flexible formatting.

**Features:**
- Multi-part address (street, city, state, zip, country, apartment)
- Single-line address support
- Country-specific restrictions
- Address autocomplete support
- Configurable components

```java
AddressField field = new AddressField();
field.setFormatMultipart(true);
field.setIncludeApartment(true);
field.setEnableAutocomplete(true);
```

### PhoneField
Extends FormField for phone number input with international support.

**Features:**
- Country-specific formatting
- Country code selector
- International format support
- Phone number validation
- Country flag display

```java
PhoneField field = new PhoneField();
field.setDefaultCountryCode("US");
field.setIncludeCountrySelector(true);
field.setEnableInternationalFormat(true);
field.setValidateActualNumber(false);
```

### CreditCardField
Extends FormField for payment card input with security focus.

**Features:**
- Card type restriction (VISA, MASTERCARD, AMEX, DISCOVER)
- Multi-part format (card, holder, expiry, CVV)
- Billing address integration
- Card preview
- 3D Secure support
- Card data tokenization (never store raw card data)

```java
CreditCardField field = new CreditCardField();
field.setAllowedCardTypes("VISA,MASTERCARD,AMEX");
field.setIncludeBillingAddress(true);
field.setTokenizeCardData(true);
field.setEnable3dSecure(true);
```

### RatingField
Extends FormField for rating/feedback input.

**Features:**
- Multiple rating types (STAR, EMOJI, NUMBER, SMILEY)
- Half-star support
- Custom labels (poor to excellent)
- Icon customization
- Visual feedback

```java
RatingField field = new RatingField();
field.setRatingType("STAR");
field.setMaxRating(5);
field.setAllowHalfRating(true);
field.setShowLabels(true);
```

### RepeatField
Extends FormField for repeating field groups (arrays).

**Features:**
- Min/max instances
- Add/remove buttons
- Reordering support
- Field preview in summary
- Dynamic field management

```java
RepeatField field = new RepeatField();
field.setMinInstances(1);
field.setMaxInstances(10);
field.setAllowAdd(true);
field.setAllowRemove(true);
field.setShowCounter(true);
```

## FormSubmission
Tracks all form submissions with metadata.

**Properties:**
- `formConfig` - Reference to form definition
- `submissionData` - JSON containing submitted values
- `submittedBy` - User email or anonymous
- `submittedAt` - Submission timestamp
- `ipAddress` - Submitter IP
- `userAgent` - Browser info
- `status` - PENDING, CONFIRMED, REJECTED, PROCESSED
- `notes` - Admin notes

## Usage Examples

### Creating a Simple Form

```java
FormConfig form = new FormConfig();
form.setFormName("contact_form");
form.setFormTitle("Contact Us");
form.setDescription("Send us your inquiries");
form.setIsPublic(true);

// Add text field
FormField nameField = new FormField();
nameField.setFieldName("name");
nameField.setLabel("Full Name");
nameField.setType(FieldType.TEXT);
nameField.setIsRequired(true);
nameField.setDisplayOrder(0);

// Add email field
FormField emailField = new FormField();
emailField.setFieldName("email");
emailField.setLabel("Email Address");
emailField.setType(FieldType.EMAIL);
emailField.setIsRequired(true);
emailField.setDisplayOrder(1);

form.getFields().add(nameField);
form.getFields().add(emailField);
```

### Creating a Form with Advanced Fields

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
cardField.setIncludeBillingAddress(false);
cardField.setTokenizeCardData(true);
cardField.setDisplayOrder(1);

paymentForm.getFields().add(addressField);
paymentForm.getFields().add(cardField);
```

### Creating a Form with Repeating Fields

```java
FormConfig orderForm = new FormConfig();
orderForm.setFormName("order_form");
orderForm.setFormTitle("Order Items");

// Create repeat field for items
RepeatField itemsRepeat = new RepeatField();
itemsRepeat.setFieldName("items");
itemsRepeat.setLabel("Order Items");
itemsRepeat.setMinInstances(1);
itemsRepeat.setMaxInstances(20);
itemsRepeat.setDisplayOrder(0);

// Add sub-fields (product name, quantity, price)
FormField productField = new FormField();
productField.setFieldName("product_name");
productField.setLabel("Product Name");
productField.setType(FieldType.TEXT);
productField.setDisplayOrder(0);

FormField quantityField = new FormField();
quantityField.setFieldName("quantity");
quantityField.setLabel("Quantity");
quantityField.setType(FieldType.NUMBER);
quantityField.setDisplayOrder(1);

itemsRepeat.getSubFields().add(productField);
itemsRepeat.getSubFields().add(quantityField);

orderForm.getFields().add(itemsRepeat);
```

### Adding Validation

```java
FormField emailField = new FormField();
emailField.setFieldName("email");
emailField.setLabel("Email");
emailField.setType(FieldType.EMAIL);

// Add validators
FieldValidator requiredValidator = new FieldValidator();
requiredValidator.setType(ValidationType.REQUIRED);
requiredValidator.setErrorMessage("Email is required");

FieldValidator emailValidator = new FieldValidator();
emailValidator.setType(ValidationType.EMAIL);
emailValidator.setErrorMessage("Please enter a valid email");

emailField.getValidators().add(requiredValidator);
emailField.getValidators().add(emailValidator);
```

## Database Schema Notes

- Uses JOINED inheritance strategy for FormField specialization
- CASCADE delete enabled for related entities
- EAGER loading for validators and options
- LAZY loading for nested fields to optimize queries
- Timestamps automatically managed with @PrePersist and @PreUpdate
- JSON storage for flexible submission data

## Future Extensions

1. **Conditional Logic** - Show/hide fields based on other field values
2. **Calculated Fields** - Auto-compute values from other fields
3. **File Upload** - Secure file attachment handling
4. **Payment Integration** - Direct Stripe/PayPal integration
5. **Multi-language Support** - Internationalized forms
6. **Template Library** - Pre-built form templates
7. **Analytics** - Form completion rates and drop-off points
8. **A/B Testing** - Multiple form variants
9. **Webhooks** - Real-time submission notifications
10. **API Keys** - For programmatic form access


