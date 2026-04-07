//package com.zekiloni.george.provisioning.infrastructure.integration.btcpay.client;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.client.RestTemplate;
//
//@Configuration
//@RequiredArgsConstructor
//public class BtcPayApiClientConfig {
//    @Value("${btcpay.base-url}")
//    private String baseUrl;
//
//    @Value("${btcpay.api-key}")
//    private String apiKey;
//
//    private RestTemplate restTemplate;
//
//    @Bean
//    public RestTemplate btcPayRestTemplate() {
//        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.getInterceptors().add((request, body, execution) -> {
//            request.getHeaders().set("Authorization", "token " + apiKey);
//            request.getHeaders().set("Content-Type", "application/json");
//            return execution.execute(request, body);
//        });
//        return restTemplate;
//    }
//
//}
