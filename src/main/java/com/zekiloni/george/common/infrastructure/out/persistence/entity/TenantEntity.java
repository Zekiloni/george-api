package com.zekiloni.george.common.infrastructure.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.TenantId;


@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
public abstract class TenantEntity extends BaseEntity {

    @Getter
    @TenantId
    @Column(name = "tenant_id")
    private String tenantId;
}
