package com.zekiloni.george.commerce.domain.order.service.invoice;

import com.zekiloni.george.commerce.application.port.in.InvoiceQueryUseCase;
import com.zekiloni.george.commerce.application.port.in.InvoiceUpdateUseCase;
import com.zekiloni.george.commerce.application.port.in.OrderQueryUseCase;
import com.zekiloni.george.commerce.application.port.in.OrderUpdateUseCase;
import com.zekiloni.george.commerce.domain.order.model.Order;
import com.zekiloni.george.commerce.domain.order.model.OrderStatus;
import com.zekiloni.george.commerce.domain.order.model.invoice.Invoice;
import com.zekiloni.george.commerce.domain.order.model.invoice.InvoiceStatus;
import com.zekiloni.george.commerce.domain.order.model.invoice.event.InvoiceExpiredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoiceExpiredEventHandler implements InvoiceEventHandler<InvoiceExpiredEvent> {
    private final OrderQueryUseCase orderQueryUseCase;
    private final OrderUpdateUseCase orderUpdateUseCase;
    private final InvoiceQueryUseCase invoiceQueryUseCase;
    private final InvoiceUpdateUseCase invoiceUpdateUseCase;

    @Override
    public Class<InvoiceExpiredEvent> getEventType() {
        return InvoiceExpiredEvent.class;
    }

    @Override
    public void handle(InvoiceExpiredEvent event) {
        invoiceQueryUseCase.getByOrderId(event.getOrderId())
                .ifPresent(this::updateInvoice);

        orderQueryUseCase.getById(event.getOrderId())
                .ifPresent(this::updateOrder);
    }

    private void updateInvoice(Invoice invoice) {
        log.info("Updating invoice {} status to EXPIRED", invoice.getId());
        invoice.setStatus(InvoiceStatus.FAILED);
        invoice.setCancelledAt(OffsetDateTime.now());
        invoiceUpdateUseCase.update(invoice);
    }

    private void updateOrder(Order order) {
        log.info("Updating order {} status to FAILED", order.getId());
        order.setStatus(OrderStatus.FAILED);
        orderUpdateUseCase.update(order);
    }
}
