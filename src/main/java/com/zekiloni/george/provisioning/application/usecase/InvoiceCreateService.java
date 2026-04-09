package com.zekiloni.george.provisioning.application.usecase;

import com.zekiloni.george.common.domain.model.Money;
import com.zekiloni.george.provisioning.application.port.in.InvoiceCreateUseCase;
import com.zekiloni.george.provisioning.application.port.out.InvoiceRepositoryPort;
import com.zekiloni.george.provisioning.domain.catalog.model.Offering;
import com.zekiloni.george.provisioning.domain.order.model.Order;
import com.zekiloni.george.provisioning.domain.order.model.OrderItem;
import com.zekiloni.george.provisioning.domain.order.model.invoice.Invoice;
import com.zekiloni.george.provisioning.domain.order.model.invoice.InvoiceItem;
import com.zekiloni.george.provisioning.domain.order.model.invoice.InvoiceStatus;
import com.zekiloni.george.provisioning.infrastructure.integration.btcpay.client.BtcPayApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceCreateService implements InvoiceCreateUseCase {
    public static final int INVOICE_EXPIRATION_TIME_MINUTES = 15;

    private final InvoiceRepositoryPort repository;
    private final BtcPayApiClient btcPayApiClient;

    @Override
    public Invoice create(Order order) {
        Invoice invoice = buildInvoice(order);
        Money total = invoice.getItems().stream()
                .map(InvoiceItem::getTotal)
                .reduce(Money.ZERO, Money::add);

        String externalInvoiceId = btcPayApiClient
                .createInvoice(order.getId(), "new",
                        total.getAmount().toPlainString(), total.getCurrency().getCurrencyCode())
                .id();


        invoice.setInvoiceNumber(externalInvoiceId);
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
                .unitPrice(offering.getPrice())
                .discountAmount(null)
                .build();
    }
}
