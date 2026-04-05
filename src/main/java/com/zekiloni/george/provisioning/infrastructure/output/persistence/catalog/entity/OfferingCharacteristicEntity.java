package com.zekiloni.george.provisioning.infrastructure.output.persistence.catalog.entity;

import com.zekiloni.george.common.infrastructure.out.persistence.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "offering_characteristics")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OfferingCharacteristicEntity extends BaseEntity {

    @Column(name = "key", nullable = false)
    private String key;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Object value;
}

