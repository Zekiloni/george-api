package com.zekiloni.george.infrastructure.adapter.out.persistence.common.entity;

import org.hibernate.annotations.TenantId;

public class TenantEntity {

    @TenantId
    private String tenantId;
}
