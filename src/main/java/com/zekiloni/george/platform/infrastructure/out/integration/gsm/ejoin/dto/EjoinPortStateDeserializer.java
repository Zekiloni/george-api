package com.zekiloni.george.platform.infrastructure.out.integration.gsm.ejoin.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Arrays;

public class EjoinPortStateDeserializer extends JsonDeserializer<EjoinPortState> {

    @Override
    public EjoinPortState deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        int code = Integer.parseInt(p.getText().trim());
        return Arrays.stream(EjoinPortState.values())
                .filter(s -> s.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown port state code: " + code));
    }
}