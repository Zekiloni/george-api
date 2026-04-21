package com.zekiloni.george.platform.infrastructure.out.integration.gsm.ejoin;

import com.zekiloni.george.platform.infrastructure.out.integration.gsm.ejoin.dto.EjoinPort;
import com.zekiloni.george.platform.infrastructure.out.integration.gsm.ejoin.dto.EjoinPortStatusResponse;
import com.zekiloni.george.platform.infrastructure.out.integration.gsm.ejoin.dto.EjoinPortsBody;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EjoinApiClient {

    private RestClient getApiClient(String baseUrl, String username, String password) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor((request, body, execution) -> {
                    URI original = request.getURI();
                    URI modified = UriComponentsBuilder.fromUri(original)
                            .queryParam("username", username)
                            .queryParam("password", password)
                            .build().toUri();
                    return execution.execute(new HttpRequestWrapper(request) {
                        @Override
                        public URI getURI() { return modified; }
                    }, body);
                })
                .build();
    }

    public EjoinPortStatusResponse getPortSTatus(String baseUrl, String username, String password, Integer port, Integer slot) {
        return postPortStatus(baseUrl, username, password,
                new EjoinPortsBody(List.of(new EjoinPort(port, slot)), null))
                .stream()
                .findFirst()
                .orElseThrow(() -> new EjoinValidationException("No status returned for port " + port));
    }

    public List<EjoinPortStatusResponse> getAllPortStatus(String baseUrl, String username, String password) {
        return postPortStatus(baseUrl, username, password,
                new EjoinPortsBody(null, "*"));
    }

    private List<EjoinPortStatusResponse> postPortStatus(String baseUrl, String username, String password, EjoinPortsBody body) {
        return getApiClient(baseUrl, username, password)
                .post()
                .uri("/get_port_status")
                .body(body)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
