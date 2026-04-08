package com.zekiloni.george.provisioning.domain.order.service.invoice;

import com.zekiloni.george.provisioning.application.port.in.OrderQueryUseCase;
import com.zekiloni.george.provisioning.application.port.in.OrderUpdateUseCase;
import com.zekiloni.george.provisioning.domain.order.model.Order;
import com.zekiloni.george.provisioning.domain.order.model.OrderStatus;
import com.zekiloni.george.provisioning.domain.order.model.invoice.event.InvoiceSettledEvent;
import com.zekiloni.george.provisioning.domain.order.service.OrderProvisioningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoiceSettledEventHandler implements InvoiceEventHandler<InvoiceSettledEvent> {
    private final OrderQueryUseCase orderQueryUseCase;
    private final OrderProvisioningService orderProvisioningService;
    private final OrderUpdateUseCase orderUpdateUseCase;

    @Override
    public Class<InvoiceSettledEvent> getEventType() {
        return InvoiceSettledEvent.class;
    }

    @Override
    public void handle(InvoiceSettledEvent event) {
        orderQueryUseCase.getById(event.getOrderId())
                .ifPresent(this::update);
    }

    private void update(Order order) {
        orderProvisioningService.handle(order);
        order.setStatus(OrderStatus.COMPLETED);
        orderUpdateUseCase.update(order);
    }
}
