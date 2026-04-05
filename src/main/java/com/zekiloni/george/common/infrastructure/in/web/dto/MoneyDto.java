package com.zekiloni.george.common.infrastructure.in.web.dto;

import java.math.BigDecimal;
import java.util.Currency;

public record MoneyDto(Currency currency, BigDecimal amount) {
}
