package com.zekiloni.george.workspace.domain.campaign.form.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing an option for select, radio, or multi-select fields.
 */
@Entity
@Table(name = "field_options")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private String value;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "is_default")
    private Boolean isDefault = false;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(columnDefinition = "TEXT")
    private String description;
}

