package com.zekiloni.george.infrastructure.persistence.common.entity;

import lombok.Getter;
import org.hibernate.annotations.TenantId;

@Getter
public class TenantEntity {

    @TenantId
    private String tenantId;
}
