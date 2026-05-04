package com.zekiloni.george.commerce.infrastructure.in.web.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zekiloni.george.commerce.application.port.in.InvoiceEventHandleUseCase;
import com.zekiloni.george.commerce.application.port.in.InvoiceQueryUseCase;
import com.zekiloni.george.commerce.application.port.out.ExternalInvoicePort;
import com.zekiloni.george.commerce.domain.order.model.invoice.Invoice;
import com.zekiloni.george.commerce.infrastructure.in.web.order.dto.InvoiceDto;
import com.zekiloni.george.commerce.infrastructure.in.web.order.dto.InvoicePaymentStatusDto;
import com.zekiloni.george.commerce.infrastructure.in.web.order.dto.event.BtcPayEventDto;
import com.zekiloni.george.commerce.infrastructure.in.web.order.mapper.InvoiceDtoMapper;
import com.zekiloni.george.commerce.infrastructure.out.persistence.order.entity.InvoiceSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.base.path:/api/v1}/invoice")
@RequiredArgsConstructor
@Slf4j
public class InvoiceApiController {
    private final InvoiceEventHandleUseCase processUseCase;
    private final InvoiceQueryUseCase queryUseCase;
    private final ExternalInvoicePort externalInvoicePort;
    private final InvoiceDtoMapper mapper;
    private final BtcPayWebhookVerifier webhookVerifier;
    private final ObjectMapper objectMapper;

    /**
     * BTCPay sends each webhook signed with HMAC-SHA256 in the {@code BTCPay-Sig}
     * header. We accept the raw body here so the signature can be validated
     * byte-for-byte before we deserialize and act on it.
     */
    @PostMapping("/webhook")
    public ResponseEntity<Object> handle(
            @RequestHeader(value = "BTCPay-Sig", required = false) String signature,
            @RequestBody String rawBody) {
        if (!webhookVerifier.verify(signature, rawBody)) {
            log.warn("Rejected BTCPay webhook with invalid or missing signature");
            return ResponseEntity.status(401).build();
        }
        try {
            BtcPayEventDto event = objectMapper.readValue(rawBody, BtcPayEventDto.class);
            processUseCase.handle(mapper.toDomain(event));
        } catch (Exception e) {
            log.error("BTCPay webhook processing failed: {}", e.getMessage(), e);
            return ResponseEntity.status(400).build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<InvoiceDto>> getAll(Pageable pageable, InvoiceSpecification specification) {
        return ResponseEntity.ok(queryUseCase.getAll(pageable, specification).map(mapper::toDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDto> getById(@PathVariable String id) {
        return queryUseCase.getById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<InvoiceDto> getByOrderId(@PathVariable String orderId) {
        return queryUseCase.getByOrderId(orderId)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Live payment-status proxy. Pulls the current state of the invoice from
     * the external provider (BTCPay) — addresses, amounts due, status, expiry —
     * so the customer's payment screen can render QR + address + countdown
     * without exposing BTCPay credentials to the browser.
     */
    @GetMapping("/{id}/payment-status")
    public ResponseEntity<InvoicePaymentStatusDto> getPaymentStatus(@PathVariable String id) {
        Invoice invoice = queryUseCase.getById(id).orElse(null);
        if (invoice == null || invoice.getInvoiceNumber() == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            ExternalInvoicePort.ExternalInvoiceStatus s =
                    externalInvoicePort.getInvoiceStatus(invoice.getInvoiceNumber());
            return ResponseEntity.ok(new InvoicePaymentStatusDto(
                    s.invoiceId(),
                    s.status(),
                    s.expiresAt(),
                    s.paymentMethods().stream()
                            .map(m -> new InvoicePaymentStatusDto.PaymentMethodDto(
                                    m.paymentMethod(),
                                    m.destination(),
                                    m.paymentLink(),
                                    m.rate(),
                                    m.due(),
                                    m.amount(),
                                    m.totalPaid()
                            ))
                            .toList()
            ));
        } catch (Exception e) {
            log.warn("Failed to fetch payment status for invoice {}: {}", id, e.getMessage());
            return ResponseEntity.status(502).build();
        }
    }
}
