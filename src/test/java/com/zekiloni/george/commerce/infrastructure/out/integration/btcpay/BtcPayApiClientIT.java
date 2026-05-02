package com.zekiloni.george.commerce.infrastructure.out.integration.btcpay;

import com.zekiloni.george.commerce.application.port.out.ExternalInvoicePort;
import com.zekiloni.george.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies the BTCPay HTTP client wire shape against a WireMock server.
 *
 * <p>This is the kind of test where a container would be overkill — we're checking
 * "are we POSTing the right JSON to the right path with the right Authorization header,
 * and parsing the response field-by-field?". WireMock makes those assertions explicit
 * and adds ~ms to the run.
 */
class BtcPayApiClientIT extends AbstractIntegrationTest {

    @Autowired
    private ExternalInvoicePort port;

    @Test
    void createInvoiceSendsExpectedPayloadAndParsesResponse() {
        btcPayMock().stubFor(post(urlPathMatching("/api/v1/stores/.*/invoices"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                              "id": "btcpay-inv-123",
                              "checkoutLink": "https://btcpay.example/i/btcpay-inv-123",
                              "status": "New"
                            }
                            """)));

        ExternalInvoicePort.ExternalInvoice invoice =
                port.createInvoice("order-42", "test order", "19.99", "USD");

        assertThat(invoice.invoiceId()).isEqualTo("btcpay-inv-123");
        assertThat(invoice.paymentUrl()).isEqualTo("https://btcpay.example/i/btcpay-inv-123");

        // Verify the wire format: api-key auth + amount + currency + metadata.orderId actually went out.
        btcPayMock().verify(postRequestedFor(urlPathMatching("/api/v1/stores/.*/invoices"))
                .withHeader("Authorization", equalTo("token test-key"))
                .withRequestBody(matchingJsonPath("$.amount", equalTo("19.99")))
                .withRequestBody(matchingJsonPath("$.currency", equalTo("USD")))
                .withRequestBody(matchingJsonPath("$.metadata.orderId", equalTo("order-42"))));
    }

    @Test
    void serverErrorBubblesUp() {
        btcPayMock().stubFor(post(urlPathMatching("/api/v1/stores/.*/invoices"))
                .willReturn(serverError().withBody("BTCPay exploded")));

        assertThatThrownBy(() -> port.createInvoice("order-42", "test", "10.00", "USD"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("BTCPay exploded");
    }
}
