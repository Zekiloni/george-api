package com.zekiloni.george.provisioning.infrastructure.out.integration.btcpay.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class BtcPayApiClientConfig {
    @Value("${btc-pay.api.base-url}")
    private String baseUrl;

    @Value("${btc-pay.api.api-key}")
    private String apiKey;

    @Bean
    public RestClient btcPayRestClient() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", String.format("token %s", apiKey))
                .build();
    }

}
