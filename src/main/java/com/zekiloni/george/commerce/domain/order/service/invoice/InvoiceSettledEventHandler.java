package com.zekiloni.george.commerce.domain.order.service.invoice;

import com.zekiloni.george.commerce.application.port.out.InvoiceRepositoryPort;
import com.zekiloni.george.commerce.application.usecase.InvoiceSettlementService;
import com.zekiloni.george.commerce.domain.order.model.invoice.event.InvoiceSettledEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * BTCPay's canonical "invoice fully settled" signal — always settle on this event.
 * Idempotency is enforced inside {@link InvoiceSettlementService#settle}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InvoiceSettledEventHandler implements InvoiceEventHandler<InvoiceSettledEvent> {

    private final InvoiceRepositoryPort invoiceRepository;
    private final InvoiceSettlementService settlementService;

    @Override
    public Class<InvoiceSettledEvent> getEventType() {
        return InvoiceSettledEvent.class;
    }

    @Override
    public void handle(InvoiceSettledEvent event) {
        invoiceRepository.findByInvoiceNumber(event.getInvoiceId())
                .ifPresentOrElse(settlementService::settle,
                        () -> log(event));
    }

    private void log(InvoiceSettledEvent event) {
        log.warn("Received settlement event for unknown invoice {}", event.getInvoiceId());
    }
}
