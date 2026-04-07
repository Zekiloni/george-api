package com.zekiloni.george.common.infrastructure.out.persistence.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.TenantId;


@SuperBuilder
@NoArgsConstructor
public abstract class TenantEntity extends BaseEntity {

    @Getter
    @TenantId
    private String tenantId;
}
