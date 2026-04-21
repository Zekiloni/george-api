package com.zekiloni.george.commerce.infrastructure.out.integration.btcpay.client;

import com.zekiloni.george.commerce.application.port.out.ExternalInvoicePort;
import com.zekiloni.george.commerce.infrastructure.out.integration.btcpay.dto.BtcPayInvoiceCreateDto;
import com.zekiloni.george.commerce.infrastructure.out.integration.btcpay.dto.BtcPayInvoiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class BtcPayApiClientAdapter implements ExternalInvoicePort {
    private final RestClient btcPayRestClient;

    @Value("${btc-pay.store-id}")
    private String storeId;

    @Value("${btc-pay.api.api-key}")
    private String apiKey;

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
}
