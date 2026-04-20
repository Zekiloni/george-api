package com.zekiloni.george.platform.infrastructure.out.messaging.gsm;

import com.zekiloni.george.platform.application.port.out.SendSmsPort;
import com.zekiloni.george.platform.domain.model.gsm.GsmProtocol;
import com.zekiloni.george.platform.domain.model.gsm.SimCard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsmpp.bean.*;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.session.SubmitSmResult;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmppSendSmsAdapter implements SendSmsPort {
    public static final String SMS = "SMS";
    private final SmppSessionPool sessionPool;

    @Override
    public boolean supports(SimCard simCard) {
        return simCard.getGateway().getProtocol() == GsmProtocol.SMPP;
    }

    @Override
    public void send(SimCard sim, String recipient, String content) throws Exception {
        SMPPSession session = sessionPool.getSession(sim.getGateway());

        byte[] textBytes = content.getBytes(StandardCharsets.UTF_8);
        DataCoding dataCoding = new GeneralDataCoding(Alphabet.ALPHA_DEFAULT, MessageClass.CLASS1, false);

        SubmitSmResult result = session.submitShortMessage(
                SMS, // Service Type
                TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, sim.getPhoneNumber(), // Source
                TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.UNKNOWN, recipient, // Destination
                new ESMClass(),
                (byte) 0, // Protocol ID
                (byte) 1, // Priority Flag
                null,    // Schedule Delivery Time
                null,    // Validity Period
                new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT), // Tražimo potvrdu o isporuci
                (byte) 0, // Replace If Present Flag
                dataCoding,
                (byte) 0, // Default Msg ID
                textBytes
        );

        log.info("SMS sent via SMPP: messageId={}, recipient={}, content={}", result.getMessageId(), recipient, content);
    }
}
