package com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.form.field;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("CREDIT_CARD")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CreditCardFieldEntity extends FormFieldEntity {

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
    private Boolean formatMultipart = true;

    @Column(name = "allowed_card_types")
    private String allowedCardTypes;

    @Column(name = "show_card_preview")
    private Boolean showCardPreview = true;

    @Column(name = "require_billing_address")
    private Boolean requireBillingAddress = false;

    @Column(name = "enable_3d_secure")
    private Boolean enable3dSecure = false;
}

