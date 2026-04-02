package com.zekiloni.george.domain.form.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a validation rule for a form field.
 * Each field can have multiple validators.
 */
@Entity
@Table(name = "field_validators")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldValidator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ValidationType type;

    @Column(name = "validator_value")
    private String value;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "is_active")
    private Boolean isActive = true;
}

