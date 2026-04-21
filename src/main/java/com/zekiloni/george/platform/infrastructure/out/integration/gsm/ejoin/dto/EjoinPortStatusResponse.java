package com.zekiloni.george.platform.infrastructure.out.integration.gsm.ejoin.dto;// infrastructure/out/integration/ejoin/dto/EjoinPortStatusResponse.java

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EjoinPortStatusResponse(

    @JsonProperty("type")
    String type,

    @JsonProperty("port")
    Double port,

    @JsonProperty("sim")
    String sim,

    @JsonProperty("seq")
    Integer seq,

    @JsonProperty("st")
    String st,

    @JsonProperty("bal")
    BigDecimal balance,

    @JsonProperty("opr")
    String operator,

    @JsonProperty("sn")
    String serialNumber,

    @JsonProperty("imei")
    String imei,

    @JsonProperty("active")
    Integer active,

    @JsonProperty("imsi")
    String imsi,

    @JsonProperty("iccid")
    String iccid,

    @JsonProperty("inserted")
    Integer inserted,

    @JsonProperty("slot_active")
    Integer slotActive,

    @JsonProperty("sig")
    Integer signalStrength,

    @JsonProperty("led")
    Integer led
) {}