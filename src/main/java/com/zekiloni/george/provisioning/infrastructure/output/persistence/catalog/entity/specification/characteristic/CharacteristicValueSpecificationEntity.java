package com.zekiloni.george.provisioning.infrastructure.output.persistence.catalog.entity.specification.characteristic;

import com.zekiloni.george.common.domain.model.TimePeriod;
import com.zekiloni.george.common.infrastructure.out.persistence.entity.BaseEntity;
import com.zekiloni.george.common.infrastructure.out.persistence.entity.TimePeriodEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Entity
@Table(name = "characteristic_value_specifications")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CharacteristicValueSpecificationEntity extends BaseEntity {
    private Boolean isDefault;
    
    private String rangeInterval;

    private String regex;
    
    private String unitOfMeasure;
    
    private String valueFrom;
    
    private String valueTo;
    
    private String valueType;

    @Embedded
    private TimePeriodEntity validFor;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Object value;
}