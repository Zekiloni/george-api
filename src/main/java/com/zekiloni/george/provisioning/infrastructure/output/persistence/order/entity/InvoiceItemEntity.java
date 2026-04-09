package com.zekiloni.george.provisioning.infrastructure.output.persistence.order.entity;

import com.zekiloni.george.common.domain.model.Money;
import com.zekiloni.george.common.infrastructure.out.persistence.entity.TenantEntity;
import com.zekiloni.george.provisioning.domain.catalog.model.Offering;
import com.zekiloni.george.provisioning.domain.order.model.invoice.InvoiceItem;
import com.zekiloni.george.provisioning.domain.order.model.invoice.InvoiceStatus;
import com.zekiloni.george.provisioning.infrastructure.output.persistence.catalog.entity.OfferingEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "invoice_items")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class InvoiceItemEntity extends TenantEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offering_id", nullable = false)
    private OfferingEntity offering;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Embedded
    private Money unitPrice;

    private BigDecimal discountAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private InvoiceEntity invoice;
}
