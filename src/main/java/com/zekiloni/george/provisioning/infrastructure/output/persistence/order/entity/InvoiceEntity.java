package com.zekiloni.george.provisioning.infrastructure.output.persistence.order.entity;

import com.zekiloni.george.common.infrastructure.out.persistence.entity.TenantEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

@Entity
public class InvoiceEntity extends TenantEntity {

    @OneToOne(mappedBy = "invoice")
    private OrderEntity order;
}
