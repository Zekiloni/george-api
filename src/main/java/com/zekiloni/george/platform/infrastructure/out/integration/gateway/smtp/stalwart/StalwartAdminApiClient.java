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

    public void createPrincipal(String adminUrl, String authHeader,
                                String name, String password, String email) {
        // "user" role grants SMTP/IMAP/JMAP submission permissions; without it
        // the principal authenticates but can't actually send or read mail.
        client(adminUrl, authHeader)
                .post()
                .uri("/api/principal")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CreatePrincipalRequest(
                        "individual", name, List.of(password), List.of(email), List.of("user")))
                .retrieve()
                .toBodilessEntity();
    }

    public void deletePrincipal(String adminUrl, String authHeader, String name) {
        client(adminUrl, authHeader)
                .delete()
                .uri("/api/principal/{name}", name)
                .retrieve()
                .toBodilessEntity();
    }

    private RestClient client(String adminUrl, String authHeader) {
        return RestClient.builder()
                .baseUrl(adminUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, authHeader)
                .build();
    }

    /** Build a Bearer header value from a Stalwart API token. */
    public static String bearer(String apiKey) {
        return "Bearer " + apiKey;
    }

    /** Build a Basic header value from admin credentials. */
    public static String basic(String username, String password) {
        String encoded = Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
        return "Basic " + encoded;
    }

    private record CreatePrincipalRequest(
            String type,
            String name,
            List<String> secrets,
            List<String> emails,
            List<String> roles
    ) {}
}
