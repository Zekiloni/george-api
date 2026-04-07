package com.zekiloni.george.provisioning.infrastructure.input.web.order;

import com.zekiloni.george.provisioning.application.port.in.InvoiceEventHandleUseCase;
import com.zekiloni.george.provisioning.infrastructure.input.web.order.dto.event.BtcPayEventDto;
import com.zekiloni.george.provisioning.infrastructure.input.web.order.mapper.InvoiceEventDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("${api.base.path:/api/v1}/invoice")
@RequiredArgsConstructor
public class InvoiceApiController {
    private final InvoiceEventHandleUseCase processUseCase;
    private final InvoiceEventDtoMapper mapper;

    @PostMapping("/webhook")
    public ResponseEntity<Object> handleInvoiceWebhook(@RequestBody BtcPayEventDto event) {
        processUseCase.handle(mapper.toDomain(event));
        return ResponseEntity.ok().build();
    }
}
