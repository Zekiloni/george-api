package com.zekiloni.george.infrastructure.persistence.billing.entity;

import com.zekiloni.george.infrastructure.persistence.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@Table(name = "plan_features")
public class PlanFeatureEntity extends BaseEntity {
    @Column(name = "key")
    private String key;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Object value;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private PlanEntity plan;
}