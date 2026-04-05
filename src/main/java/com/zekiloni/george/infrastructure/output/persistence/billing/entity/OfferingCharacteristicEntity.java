package com.zekiloni.george.infrastructure.output.persistence.billing.entity;

import com.zekiloni.george.infrastructure.output.persistence.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@Table(name = "offering_characteristics")
public class OfferingCharacteristicEntity extends BaseEntity {
    @Column(name = "key")
    private String key;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Object value;
}

