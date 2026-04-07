package com.zekiloni.george.provisioning.domain.order.service.invoice;

import com.zekiloni.george.provisioning.application.port.in.OrderQueryUseCase;
import com.zekiloni.george.provisioning.application.port.in.OrderUpdateUseCase;
import com.zekiloni.george.provisioning.domain.order.model.Order;
import com.zekiloni.george.provisioning.domain.order.model.OrderStatus;
import com.zekiloni.george.provisioning.domain.order.model.invoice.event.InvoiceExpiredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoiceExpiredEventHandler implements InvoiceEventHandler<InvoiceExpiredEvent> {
    private final OrderQueryUseCase orderQueryUseCase;
    private final OrderUpdateUseCase orderUpdateUseCase;

    @Override
    public Class<InvoiceExpiredEvent> getEventType() {
        return InvoiceExpiredEvent.class;
    }

    @Override
    public void handle(InvoiceExpiredEvent event) {
        orderQueryUseCase.getById(event.getOrderId())
                .ifPresent(this::update);
    }

    private void update(Order order) {
        // TODO: Update invoice status to FAILED, EXPIRED
        log.info("Updating order {} status to FAILED", order.getId());
        order.setStatus(OrderStatus.FAILED);
        orderUpdateUseCase.update(order);
    }
}
