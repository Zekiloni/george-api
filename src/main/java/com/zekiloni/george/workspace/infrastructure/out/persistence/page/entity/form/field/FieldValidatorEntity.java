package com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.form.field;

import com.zekiloni.george.common.infrastructure.out.persistence.entity.BaseEntity;
import com.zekiloni.george.workspace.domain.page.form.field.ValidationType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "field_validators")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString
public class FieldValidatorEntity extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ValidationType type;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Object value;

    @Column(name = "error_message")
    private String errorMessage;
}

