package com.zekiloni.george.provisioning.infrastructure.input.web.catalog;

import com.zekiloni.george.provisioning.application.port.in.OfferingCreateUseCase;
import com.zekiloni.george.provisioning.application.port.in.OfferingQueryUseCase;
import com.zekiloni.george.provisioning.infrastructure.input.web.billing.dto.OfferingCreateDto;
import com.zekiloni.george.provisioning.infrastructure.input.web.billing.dto.OfferingDto;
import com.zekiloni.george.provisioning.infrastructure.input.web.billing.mapper.OfferingDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.base.path:/api/v1}/offering")
@RequiredArgsConstructor
public class OfferingApiController {
    private final OfferingCreateUseCase createUseCase;
    private final OfferingQueryUseCase queryUseCase;
    private final OfferingDtoMapper mapper;

    @PostMapping
    public ResponseEntity<OfferingDto> createOffering(@RequestBody OfferingCreateDto offeringCreate) {
        return ResponseEntity.ok(mapper.toDto(createUseCase.handle(mapper.toDomain(offeringCreate))));
    }

    @GetMapping
    public ResponseEntity<Page<OfferingDto>> getOfferings(Pageable pageable) {
        return ResponseEntity.ok(queryUseCase.getAll(pageable).map(mapper::toDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OfferingDto> getOffering(@PathVariable String id) {
        return queryUseCase.getById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

