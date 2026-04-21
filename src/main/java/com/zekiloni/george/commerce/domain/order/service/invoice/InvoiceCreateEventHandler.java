package com.zekiloni.george.commerce.domain.order.service.invoice;

import com.zekiloni.george.commerce.application.port.in.InvoiceQueryUseCase;
import com.zekiloni.george.commerce.application.port.in.InvoiceUpdateUseCase;
import com.zekiloni.george.commerce.application.port.in.OrderQueryUseCase;
import com.zekiloni.george.commerce.application.port.in.OrderUpdateUseCase;
import com.zekiloni.george.commerce.domain.order.model.Order;
import com.zekiloni.george.commerce.domain.order.model.OrderStatus;
import com.zekiloni.george.commerce.domain.order.model.invoice.Invoice;
import com.zekiloni.george.commerce.domain.order.model.invoice.event.InvoiceCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoiceCreateEventHandler implements InvoiceEventHandler<InvoiceCreatedEvent> {
    private final OrderQueryUseCase orderQueryUseCase;
    private final OrderUpdateUseCase orderUpdateUseCase;
    private final InvoiceQueryUseCase invoiceQueryUseCase;
    private final InvoiceUpdateUseCase invoiceUpdateUseCase;

    @Override
    public Class<InvoiceCreatedEvent> getEventType() {
        return InvoiceCreatedEvent.class;
    }

    @Override
    public void handle(InvoiceCreatedEvent event) {
        orderQueryUseCase.getById(event.getOrderId())
                .ifPresent(this::updateOrder);

        invoiceQueryUseCase.getByOrderId(event.getOrderId())
                .ifPresent(invoice -> updateInvoice(invoice, event));
    }

    private void updateOrder(Order order) {
        log.info("Updating order {} status to PENDING", order.getId());
        order.setStatus(OrderStatus.PENDING);
        orderUpdateUseCase.update(order);
    }

    private void updateInvoice(Invoice invoice, InvoiceCreatedEvent event) {
        invoice.setInvoiceNumber(event.getInvoiceId());
        invoiceUpdateUseCase.update(invoice);
    }
}
