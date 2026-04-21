package com.zekiloni.george.commerce.application.usecase;

import com.zekiloni.george.common.domain.model.Money;
import com.zekiloni.george.commerce.application.port.in.InvoiceCreateUseCase;
import com.zekiloni.george.commerce.application.port.out.ExternalInvoicePort;
import com.zekiloni.george.commerce.application.port.out.InvoiceRepositoryPort;
import com.zekiloni.george.commerce.domain.catalog.model.Offering;
import com.zekiloni.george.commerce.domain.order.model.Order;
import com.zekiloni.george.commerce.domain.order.model.OrderItem;
import com.zekiloni.george.commerce.domain.order.model.invoice.Invoice;
import com.zekiloni.george.commerce.domain.order.model.invoice.InvoiceItem;
import com.zekiloni.george.commerce.domain.order.model.invoice.InvoiceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceCreateService implements InvoiceCreateUseCase {
    public static final int INVOICE_EXPIRATION_TIME_MINUTES = 15;

    private final InvoiceRepositoryPort repository;
    private final ExternalInvoicePort externalInvoicePort;

    @Override
    public Invoice create(Order order) {
        Invoice invoice = buildInvoice(order);
        Money total = invoice.getItems().stream()
                .map(InvoiceItem::getTotal)
                .reduce(Money.ZERO, Money::add);

        ExternalInvoicePort.ExternalInvoice externalInvoice = externalInvoicePort
                .createInvoice(order.getId(), "new",
                        total.getAmount().toPlainString(), total.getCurrency().getCurrencyCode());

        invoice.setInvoiceNumber(externalInvoice.invoiceId());
        invoice.setPaymentReference(externalInvoice.paymentUrl());

        return repository.save(invoice);
    }

    private Invoice buildInvoice(Order newOrder) {
        return Invoice.builder()
                .order(newOrder)
                .issuedAt(OffsetDateTime.now())
                .dueAt(OffsetDateTime.now().plusMinutes(INVOICE_EXPIRATION_TIME_MINUTES))
                .status(InvoiceStatus.PENDING)
                .items(this.buildInvoiceItems(newOrder))
                .build();
    }

    private List<InvoiceItem> buildInvoiceItems(Order order) {
        return order.getItem().stream()
                .map(this::buildInvoiceItem)
                .toList();
    }


    private InvoiceItem buildInvoiceItem(OrderItem orderItem) {
        Offering offering = orderItem.getOffering();

        return InvoiceItem.builder()
                .offering(offering)
                .quantity(orderItem.getQuantity())
                .unitPrice(offering.getPrice(orderItem.getDuration(), orderItem.getDurationUnit()))
                .discountAmount(null)
                .build();
    }
}
