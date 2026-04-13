package com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.form.field;

import com.zekiloni.george.common.infrastructure.out.persistence.entity.BaseEntity;
import com.zekiloni.george.workspace.domain.page.form.field.FieldType;
import com.zekiloni.george.workspace.domain.page.form.field.metadata.FieldMetadata;
import com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.form.FormEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "form_fields")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "field_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"validators", "options", "field", "form", "parentField"})
public class FormFieldEntity extends BaseEntity {
    @Column(name = "field_name", nullable = false)
    private String fieldName;

    @Column(name = "label")
    private String label;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, insertable = false, updatable = false)
    private FieldType type;

    @Column(name = "placeholder")
    private String placeholder;

    @Column(name = "help_text")
    private String helpText;

    @Column(name = "default_value")
    private String defaultValue;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "required", nullable = false)
    private Boolean required = false;

    @Column(name = "is_read_only", nullable = false)
    private Boolean readonly = false;

    @Column(name = "is_hidden", nullable = false)
    private Boolean hidden = false;

    @Column(name = "custom_attributes", columnDefinition = "TEXT")
    private String customAttributes;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private FieldMetadata metadata;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id", nullable = false)
    private FormEntity form;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "form_field_id")
    @Builder.Default
    private List<FieldValidatorEntity> validators = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "form_field_id")
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<FieldOptionEntity> options = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_field_id")
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<FormFieldEntity> field = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_field_id", insertable = false, updatable = false)
    private FormFieldEntity parentField;
}

