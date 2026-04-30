package com.zekiloni.george.platform.infrastructure.out.integration.gateway.smtp.stalwart;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Component
public class StalwartAdminApiClient {

    public void createPrincipal(String adminUrl, String adminUsername, String adminPassword,
                                String name, String password, String email) {
        client(adminUrl, adminUsername, adminPassword)
                .post()
                .uri("/api/principal")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CreatePrincipalRequest("individual", name, List.of(password), List.of(email)))
                .retrieve()
                .toBodilessEntity();
    }

    public void deletePrincipal(String adminUrl, String adminUsername, String adminPassword, String name) {
        client(adminUrl, adminUsername, adminPassword)
                .delete()
                .uri("/api/principal/{name}", name)
                .retrieve()
                .toBodilessEntity();
    }

    private RestClient client(String adminUrl, String username, String password) {
        String basic = Base64.getEncoder().encodeToString(
                (username + ":" + password).getBytes(StandardCharsets.UTF_8));
        return RestClient.builder()
                .baseUrl(adminUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + basic)
                .build();
    }

    private record CreatePrincipalRequest(
            String type,
            String name,
            List<String> secrets,
            List<String> emails
    ) {}
}
