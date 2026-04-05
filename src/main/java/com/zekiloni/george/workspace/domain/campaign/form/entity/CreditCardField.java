package com.zekiloni.george.workspace.domain.campaign.form.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Specialized FormField for credit card input.
 * Supports various card types and includes validation configurations.
 */
@Entity
@Table(name = "credit_card_fields")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("CREDIT_CARD")
public class CreditCardField extends FormField {

    @Column(name = "include_card_number")
    private Boolean includeCardNumber = true;

    @Column(name = "include_card_holder")
    private Boolean includeCardHolder = true;

    @Column(name = "include_expiry")
    private Boolean includeExpiry = true;

    @Column(name = "include_cvv")
    private Boolean includeCvv = true;

    @Column(name = "include_billing_address")
    private Boolean includeBillingAddress = false;

    @Column(name = "format_multipart")
    private Boolean formatMultipart = true; // If true, shows separate fields; if false, single input

    @Column(name = "allowed_card_types")
    private String allowedCardTypes; // Comma-separated: "VISA,MASTERCARD,AMEX,DISCOVER"

    @Column(name = "show_card_preview")
    private Boolean showCardPreview = true;

    @Column(name = "require_billing_address")
    private Boolean requireBillingAddress = false;

    @Column(name = "enable_3d_secure")
    private Boolean enable3dSecure = false;

    @Column(name = "tokenize_card_data")
    private Boolean tokenizeCardData = true; // Don't store raw card data
}

