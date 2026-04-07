package com.zekiloni.george.provisioning.infrastructure.integration.btcpay.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BtcPayApiClient {
//    private final BtcPayApiClientConfig config;

    public String createInvoice(String orderId, String description, String amount, String currency) {
        return null;
        // Build the request payload
//        var payload = new BtcPayInvoiceRequest(orderId, description, amount, currency);
//
//        // Send the request to create an invoice
//        var response = config.btcPayRestTemplate().postForEntity(
//                config.getBaseUrl() + "/invoices",
//                payload,
//                String.class
//        );
//
//        // Handle the response and return the invoice URL or ID
//        if (response.getStatusCode().is2xxSuccessful()) {
//            // Extract invoice URL or ID from the response body
//            return response.getBody(); // Adjust this based on actual response structure
//        } else {
//            throw new RuntimeException("Failed to create invoice: " + response.getStatusCode());
//        }
    }
}
