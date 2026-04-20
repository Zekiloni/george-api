package com.zekiloni.george.platform.infrastructure.out.messaging.gsm;

import com.zekiloni.george.platform.domain.model.gsm.GsmGateway;
import lombok.extern.slf4j.Slf4j;
import org.jsmpp.bean.*;
import org.jsmpp.extra.ProcessRequestException;
import org.jsmpp.session.*;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SmppSessionPool implements DisposableBean {
    private final Map<String, SMPPSession> sessions = new ConcurrentHashMap<>();

    public SMPPSession getSession(GsmGateway gateway) throws Exception {
        SMPPSession session = sessions.get(gateway.getIpAddress());

        if (session == null || !session.getSessionState().isBound()) {
            session = createNewSession(gateway);
            sessions.put(gateway.getIpAddress(), session);
        }
        return session;
    }

    private SMPPSession createNewSession(GsmGateway gw) throws Exception {
        SMPPSession session = new SMPPSession();
        session.setTransactionTimer(5000L);

        session.setMessageReceiverListener(new MessageReceiverListener() {

            @Override
            public DataSmResult onAcceptDataSm(DataSm dataSm, Session session) throws ProcessRequestException {
                return null;
            }

            @Override
            public void onAcceptDeliverSm(DeliverSm deliverSm) throws ProcessRequestException {
                if (MessageType.SMSC_DEL_RECEIPT.containedIn(deliverSm.getEsmClass())) {
                    try {
                        String messageText = new String(deliverSm.getShortMessage());
                        log.info("Stigao Delivery Receipt od Ejoin-a: {}", messageText);

                        // Ovde pozivaš domen da ažurira status u bazi
                        // smsOrchestrator.updateMessageStatus(messageText);

                    } catch (Exception e) {
                        log.error("Greška pri parsiranju DLR poruke", e);
                    }
                } else {
                    // 2. Ovo je regularna dolazna SMS poruka (Inbound SMS)
                    // Ako George želi da prima odgovore od kupaca, ovde ih hvataš
                    String inboundText = new String(deliverSm.getShortMessage());
                    log.info("Primljena dolazna poruka na SIM {}: {}", deliverSm.getDestAddress(), inboundText);
                }
            }

            @Override
            public void onAcceptAlertNotification(AlertNotification alertNotification) {

            }
        });

        session.connectAndBind(
                gw.getIpAddress(),
                gw.getPort(),
                new BindParameter(
                        BindType.BIND_TX,
                        gw.getUsername(),
                        gw.getPassword(),
                        "cp",
                        TypeOfNumber.UNKNOWN,
                        NumberingPlanIndicator.UNKNOWN,
                        null
                )
        );

        return session;
    }

    @Override
    public void destroy() {
        sessions.values().forEach(session -> {
            if (session.getSessionState().isBound()) {
                session.unbindAndClose();
            }
        });
    }
}