package com.zekiloni.george.platform.infrastructure.in.web.gateway.dto;

import com.zekiloni.george.platform.domain.model.gateway.smtp.SmtpGatewayProvider;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SmtpGatewayCreateDto extends GatewayCreateDto {
    @NotNull
    private SmtpGatewayProvider provider;
}
