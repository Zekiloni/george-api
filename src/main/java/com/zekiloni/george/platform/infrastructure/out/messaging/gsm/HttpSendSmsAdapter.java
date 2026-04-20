package com.zekiloni.george.platform.infrastructure.out.messaging.gsm;

import com.zekiloni.george.platform.application.port.out.SendSmsPort;
import com.zekiloni.george.platform.domain.model.gsm.GsmProtocol;
import com.zekiloni.george.platform.domain.model.gsm.SimCard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class HttpSendSmsAdapter implements SendSmsPort {
    private final RestClient restClient;

    @Override
    public boolean supports(SimCard simCard) {
        return simCard.getGateway().getProtocol() == GsmProtocol.HTTP;
    }

    @Override
    public void send(SimCard sim, String recipient, String content) throws Exception {

    }
}
