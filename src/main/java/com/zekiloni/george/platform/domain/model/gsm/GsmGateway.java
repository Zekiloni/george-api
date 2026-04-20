package com.zekiloni.george.platform.domain.model.gsm;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GsmGateway {
    private String id;
    private String ipAddress;
    private int port;
    private GsmProtocol protocol;
    private List<SimCard> simCard;
    private String username;
    private String password;
}
