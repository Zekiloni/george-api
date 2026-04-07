package com.zekiloni.george.common.infrastructure.out.persistence.entity;

import lombok.Getter;
import org.hibernate.annotations.TenantId;

@Getter
public class TenantEntity extends BaseEntity {

    @TenantId
    private String tenantId;
}
