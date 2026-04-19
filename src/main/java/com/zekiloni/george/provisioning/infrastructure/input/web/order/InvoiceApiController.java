package com.zekiloni.george.provisioning.infrastructure.input.web.order;

import com.zekiloni.george.provisioning.application.port.in.InvoiceEventHandleUseCase;
import com.zekiloni.george.provisioning.application.port.in.InvoiceQueryUseCase;
import com.zekiloni.george.provisioning.infrastructure.input.web.order.dto.InvoiceDto;
import com.zekiloni.george.provisioning.infrastructure.input.web.order.dto.event.BtcPayEventDto;
import com.zekiloni.george.provisioning.infrastructure.input.web.order.mapper.InvoiceDtoMapper;
import com.zekiloni.george.provisioning.infrastructure.output.persistence.order.entity.InvoiceSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.base.path:/api/v1}/invoice")
@RequiredArgsConstructor
public class InvoiceApiController {
    private final InvoiceEventHandleUseCase processUseCase;
    private final InvoiceQueryUseCase queryUseCase;
    private final InvoiceDtoMapper mapper;

    @PostMapping("/webhook")
    public ResponseEntity<Object> handle(@RequestBody BtcPayEventDto event) {
        processUseCase.handle(mapper.toDomain(event));
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<InvoiceDto>> getAll(Pageable pageable, InvoiceSpecification specification) {
        return ResponseEntity.ok(queryUseCase.getAll(pageable, specification).map(mapper::toDto));
    }
}
