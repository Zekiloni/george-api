package com.zekiloni.george.commerce.infrastructure.out.integration.btcpay.client;

import com.zekiloni.george.commerce.application.port.out.ExternalInvoicePort;
import com.zekiloni.george.commerce.infrastructure.out.integration.btcpay.dto.BtcPayInvoiceCreateDto;
import com.zekiloni.george.commerce.infrastructure.out.integration.btcpay.dto.BtcPayInvoiceResponse;
import com.zekiloni.george.commerce.infrastructure.out.integration.btcpay.dto.BtcPayPaymentMethodResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class BtcPayApiClientAdapter implements ExternalInvoicePort {
    private final RestClient btcPayRestClient;

    @Value("${btc-pay.store-id}")
    private String storeId;

    @Value("${btc-pay.api.api-key}")
    private String apiKey;

    @Override
    public ExternalInvoice createInvoice(String orderId, String description, String amount, String currency) {
        BtcPayInvoiceCreateDto.Metadata metadata = new BtcPayInvoiceCreateDto.Metadata(
                orderId,
                "https://example.com/orders/" + orderId,
                description,
                null,
                null
        );

        BtcPayInvoiceCreateDto btcPayInvoiceCreateDto = new BtcPayInvoiceCreateDto(metadata, null, null, amount, currency, null);

        BtcPayInvoiceResponse response = btcPayRestClient
                .post()
                .uri("/stores/{storeId}/invoices", storeId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(btcPayInvoiceCreateDto)
                .header("Authorization", String.format("token %s", apiKey))
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    String body = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
                    throw new RuntimeException("Status: " + res.getStatusCode() + " | Body: " + body);
                })
                .body(BtcPayInvoiceResponse.class);

        return new ExternalInvoice(response.id(), response.checkoutLink());
    }

    @Override
    public ExternalInvoiceStatus getInvoiceStatus(String invoiceId) {
        // Two BTCPay calls fired in parallel — each adds ~1-2s on a cold regtest
        // node, sequential they stack to ~4s and the customer's QR screen waits
        // for both. Virtual threads (spring.threads.virtual.enabled) keep the
        // overhead trivial.
        CompletableFuture<BtcPayInvoiceResponse> invoiceFuture = CompletableFuture.supplyAsync(() ->
                btcPayRestClient
                        .get()
                        .uri("/stores/{storeId}/invoices/{invoiceId}", storeId, invoiceId)
                        .header("Authorization", String.format("token %s", apiKey))
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, (req, res) -> {
                            String body = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
                            throw new RuntimeException("BTCPay invoice fetch failed: " + res.getStatusCode() + " | " + body);
                        })
                        .body(BtcPayInvoiceResponse.class));

        CompletableFuture<List<BtcPayPaymentMethodResponse>> methodsFuture = CompletableFuture.supplyAsync(() ->
                btcPayRestClient
                        .get()
                        .uri("/stores/{storeId}/invoices/{invoiceId}/payment-methods", storeId, invoiceId)
                        .header("Authorization", String.format("token %s", apiKey))
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, (req, res) -> {
                            String body = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
                            throw new RuntimeException("BTCPay payment-methods fetch failed: " + res.getStatusCode() + " | " + body);
                        })
                        .body(new ParameterizedTypeReference<List<BtcPayPaymentMethodResponse>>() {}));

        BtcPayInvoiceResponse invoice = invoiceFuture.join();
        List<BtcPayPaymentMethodResponse> methods = methodsFuture.join();

        OffsetDateTime expiresAt = invoice.expirationTime() == null
                ? null
                : OffsetDateTime.ofInstant(Instant.ofEpochSecond(invoice.expirationTime()), ZoneOffset.UTC);

        List<PaymentMethod> mapped = methods == null ? List.of() : methods.stream()
                .filter(m -> m.activated() == null || m.activated())
                .map(m -> new PaymentMethod(
                        m.paymentMethod(),
                        m.destination(),
                        m.paymentLink(),
                        m.rate(),
                        m.due(),
                        m.amount(),
                        m.totalPaid()
                ))
                .toList();

        return new ExternalInvoiceStatus(invoice.id(), invoice.status(), expiresAt, mapped);
    }
}
