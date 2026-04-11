package com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.form.field;

import com.zekiloni.george.common.infrastructure.out.persistence.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @Column(columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private Object value;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @Column(name = "description")
    private String description;
}

