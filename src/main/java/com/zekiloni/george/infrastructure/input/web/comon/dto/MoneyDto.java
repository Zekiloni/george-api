package com.zekiloni.george.infrastructure.input.web.comon.dto;

import java.math.BigDecimal;
import java.util.Currency;

public record MoneyDto(Currency currency, BigDecimal amount) {
}
