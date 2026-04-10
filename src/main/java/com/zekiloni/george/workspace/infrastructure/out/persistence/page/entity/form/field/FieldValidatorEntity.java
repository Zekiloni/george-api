package com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.form.field;

import com.zekiloni.george.common.infrastructure.out.persistence.entity.BaseEntity;
import com.zekiloni.george.workspace.domain.page.form.field.ValidationType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

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

    @Column(name = "value")
    private String value;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}

