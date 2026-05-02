package com.zekiloni.george.commerce.domain.order.service.invoice;

import com.zekiloni.george.commerce.application.port.out.InvoiceRepositoryPort;
import com.zekiloni.george.commerce.application.usecase.InvoiceSettlementService;
import com.zekiloni.george.commerce.domain.order.model.invoice.Invoice;
import com.zekiloni.george.commerce.domain.order.model.invoice.event.InvoicePaymentSettledEvent;
import com.zekiloni.george.commerce.domain.order.model.invoice.event.PaymentDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Defensive backup for {@link InvoiceSettledEventHandler}. BTCPay normally fires
 * both events when a single full payment confirms — whichever arrives first marks
 * the invoice PAID; the second one short-circuits via the idempotency guard.
 *
 * <p>This handler only settles when the payment value covers the invoice total —
 * it ignores partial payments (which we don't track cumulatively).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InvoicePaymentSettledEventHandler implements InvoiceEventHandler<InvoicePaymentSettledEvent> {

    private final InvoiceRepositoryPort invoiceRepository;
    private final InvoiceSettlementService settlementService;

    @Override
    public Class<InvoicePaymentSettledEvent> getEventType() {
        return InvoicePaymentSettledEvent.class;
    }

    @Override
    public void handle(InvoicePaymentSettledEvent event) {
        Invoice invoice = invoiceRepository.findByInvoiceNumber(event.getInvoiceId()).orElse(null);
        if (invoice == null) {
            log.warn("InvoicePaymentSettled for unknown invoice number {}", event.getInvoiceId());
            return;
        }

        if (!coversInvoice(event.getPayment(), invoice)) {
            log.debug("InvoicePaymentSettled for {} does not cover invoice total — waiting for further payments",
                    event.getInvoiceId());
            return;
        }

        settlementService.settle(invoice);
    }

    private boolean coversInvoice(PaymentDetail payment, Invoice invoice) {
        if (payment == null || payment.getValue() == null) return false;
        if (invoice.getTotal() == null) return false;
        try {
            BigDecimal paid = new BigDecimal(payment.getValue());
            return paid.compareTo(invoice.getTotal().getAmount()) >= 0;
        } catch (NumberFormatException e) {
            log.warn("InvoicePaymentSettled: unparseable payment value '{}' on invoice {}",
                    payment.getValue(), event(invoice));
            return false;
        }
    }

    private String event(Invoice invoice) {
        return invoice.getInvoiceNumber() != null ? invoice.getInvoiceNumber() : invoice.getId();
    }
}
