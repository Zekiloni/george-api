package com.zekiloni.george.platform.application.port.out;

import com.zekiloni.george.platform.domain.model.gsm.SimCard;

public interface SendSmsPort {
    boolean supports(SimCard simCard);
    void send(SimCard sim, String recipient, String content) throws Exception;
}
