package com.zekiloni.george.provisioning.application.usecase;


import com.zekiloni.george.provisioning.application.port.in.InvoiceEventHandleUseCase;
import com.zekiloni.george.provisioning.domain.order.model.invoice.event.InvoiceEvent;
import com.zekiloni.george.provisioning.domain.order.service.invoice.InvoiceEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceEventHandleService implements InvoiceEventHandleUseCase {
    private final List<InvoiceEventHandler<?>> handlers;

    @Override
    public void handle(InvoiceEvent event) {
        log.info("Handling invoice event: {}", event);
        handlers
                .stream().filter(a -> a.getEventType().isAssignableFrom(event.getClass()))
                .findFirst()
                .ifPresentOrElse(h -> handleWithTypedHandler(h, event),
                        () -> log.warn("No handler found for event type: {}", event.getClass().getName()));
    }

    @SuppressWarnings("unchecked")
    private <T extends InvoiceEvent> void handleWithTypedHandler(InvoiceEventHandler<?> handler, InvoiceEvent event) {
        InvoiceEventHandler<T> typedHandler = (InvoiceEventHandler<T>) handler;
        typedHandler.handle((T) event);
    }
}
