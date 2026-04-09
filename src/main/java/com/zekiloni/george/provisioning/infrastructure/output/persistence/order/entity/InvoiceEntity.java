package com.zekiloni.george.provisioning.infrastructure.output.persistence.order.entity;

import com.zekiloni.george.common.infrastructure.out.persistence.entity.TenantEntity;
import com.zekiloni.george.provisioning.domain.order.model.Order;
import com.zekiloni.george.provisioning.domain.order.model.invoice.InvoiceItem;
import com.zekiloni.george.provisioning.domain.order.model.invoice.InvoiceStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "invoices")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class InvoiceEntity extends TenantEntity {
    @Column(name = "invoice_number", nullable = false, unique = true)
    private String invoiceNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InvoiceStatus status;

    private List<InvoiceItem> items;

    @Column(name = "payment_reference")
    private String paymentReference;

    private OffsetDateTime issuedAt;
    private OffsetDateTime dueAt;
    private OffsetDateTime paidAt;
    private OffsetDateTime cancelledAt;
    private OffsetDateTime refundedAt;

    private String note;

    @OneToOne(mappedBy = "invoice")
    private OrderEntity order;
}
