package com.zekiloni.george.platform.infrastructure.out.integration.gateway.smtp.stalwart;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class StalwartAdminApiClient {

    public void createPrincipal(String adminUrl, String apiKey,
                                String name, String password, String email) {
        client(adminUrl, apiKey)
                .post()
                .uri("/api/principal")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CreatePrincipalRequest("individual", name, List.of(password), List.of(email)))
                .retrieve()
                .toBodilessEntity();
    }

    public void deletePrincipal(String adminUrl, String apiKey, String name) {
        client(adminUrl, apiKey)
                .delete()
                .uri("/api/principal/{name}", name)
                .retrieve()
                .toBodilessEntity();
    }

    private RestClient client(String adminUrl, String apiKey) {
        return RestClient.builder()
                .baseUrl(adminUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
    }

    private record CreatePrincipalRequest(
            String type,
            String name,
            List<String> secrets,
            List<String> emails
    ) {}
}
