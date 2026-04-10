package com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.form.field;

import com.zekiloni.george.common.infrastructure.out.persistence.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "field_options")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString
public class FieldOptionEntity extends BaseEntity {

    @Column(name = "label", nullable = false)
    private String label;

    @Column(name = "value", nullable = false)
    private String value;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "description")
    private String description;
}

