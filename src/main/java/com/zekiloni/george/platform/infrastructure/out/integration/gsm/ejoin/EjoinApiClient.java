package com.zekiloni.george.platform.infrastructure.out.integration.gsm.ejoin;

import com.zekiloni.george.platform.infrastructure.out.integration.gsm.ejoin.dto.EjoinDevStatus;
import com.zekiloni.george.platform.infrastructure.out.integration.gsm.ejoin.dto.EjoinPortStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

// EJOIN HTTP API per "EJOIN HTTP DEVELOPMENT SPEC v2.0.012": query-string auth,
// JSON bodies. Status is normally a push-subscription (GET ?url=callback&period=N
// makes the device push). Calling /goip_get_status.html WITHOUT a url returns a
// one-shot snapshot of all ports — that's what we use here.
@Component
@RequiredArgsConstructor
public class EjoinApiClient {

    private static final String STATUS_PATH   = "/goip_get_status.html";
    private static final String SEND_SMS_PATH = "/goip_post_sms.html";

    public EjoinDevStatus getStatus(String baseUrl, String username, String password) {
        return client(baseUrl)
                .get()
                .uri(uriBuilder -> uriBuilder.path(STATUS_PATH)
                        .queryParam("username", username)
                        .queryParam("password", password)
                        .build())
                .retrieve()
                .body(EjoinDevStatus.class);
    }

    public List<EjoinPortStatusResponse> getAllPortStatus(String baseUrl, String username, String password) {
        EjoinDevStatus dev = getStatus(baseUrl, username, password);
        return dev == null || dev.status() == null ? List.of() : dev.status();
    }

    public void sendSms(String baseUrl, String username, String password,
                        String portSpec, String to, String message) {
        Map<String, Object> body = Map.of(
                "type", "send-sms",
                "task_num", 1,
                "tasks", List.of(Map.of(
                        "tid", 1,
                        "from", portSpec,
                        "to", to,
                        "sms", message
                ))
        );
        client(baseUrl)
                .post()
                .uri(uriBuilder -> uriBuilder.path(SEND_SMS_PATH)
                        .queryParam("username", username)
                        .queryParam("password", password)
                        .build())
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }

    private RestClient client(String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }
}
