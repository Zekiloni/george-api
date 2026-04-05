package com.zekiloni.george.workspace.domain.campaign.form.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a form field in a form configuration.
 * Supports various field types including basic, advanced, and preset types.
 * Uses JOINED inheritance strategy for flexibility.
 */
@Entity
@Table(name = "form_fields")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "field_type", discriminatorType = DiscriminatorType.STRING)
@ToString(exclude = {"validators", "options", "subFields"})
public class FormField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String fieldName;

    @Column(nullable = false)
    private String label;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FieldType type;

    @Column(columnDefinition = "TEXT")
    private String placeholder;

    @Column(columnDefinition = "TEXT")
    private String helpText;

    @Column(name = "default_value", columnDefinition = "TEXT")
    private String defaultValue;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(name = "is_required")
    private Boolean isRequired = false;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_read_only")
    private Boolean isReadOnly = false;

    @Column(name = "is_hidden")
    private Boolean isHidden = false;

    @Column(columnDefinition = "TEXT")
    private String customAttributes;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "form_field_id")
    private List<FieldValidator> validators = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "form_field_id")
    @OrderBy("displayOrder ASC")
    private List<FieldOption> options = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_field_id")
    @OrderBy("displayOrder ASC")
    private List<FormField> subFields = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_field_id", insertable = false, updatable = false)
    private FormField parentField;
}

