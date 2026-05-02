package com.zekiloni.george.commerce.infrastructure.in.web.catalog;

import com.zekiloni.george.commerce.application.port.in.OfferingCreateUseCase;
import com.zekiloni.george.commerce.application.port.in.OfferingQueryUseCase;
import com.zekiloni.george.commerce.application.port.in.OfferingUpdateUseCase;
import com.zekiloni.george.commerce.infrastructure.in.web.catalog.dto.OfferingCreateDto;
import com.zekiloni.george.commerce.infrastructure.in.web.catalog.dto.OfferingDto;
import com.zekiloni.george.commerce.infrastructure.in.web.catalog.mapper.OfferingDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.base.path:/api/v1}/offering")
@RequiredArgsConstructor
public class OfferingApiController {
    private final OfferingCreateUseCase createUseCase;
    private final OfferingUpdateUseCase updateUseCase;
    private final OfferingQueryUseCase queryUseCase;
    private final OfferingDtoMapper mapper;

    @PreAuthorize("hasRole('admin')")
    @PostMapping
    public ResponseEntity<OfferingDto> createOffering(@RequestBody OfferingCreateDto offeringCreate) {
        return ResponseEntity.ok(mapper.toDto(createUseCase.create(mapper.toDomain(offeringCreate))));
    }

    @PreAuthorize("hasRole('admin')")
    @PutMapping("/{id}")
    public ResponseEntity<OfferingDto> updateOffering(@PathVariable String id,
                                                      @RequestBody OfferingCreateDto offeringUpdate) {
        return ResponseEntity.ok(mapper.toDto(updateUseCase.update(id, mapper.toDomain(offeringUpdate))));
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping
    public ResponseEntity<Page<OfferingDto>> getOfferings(Pageable pageable) {
        return ResponseEntity.ok(queryUseCase.getAll(pageable).map(mapper::toDto));
    }

    @GetMapping("/active")
    public ResponseEntity<List<OfferingDto>> getActiveOfferings() {
        return ResponseEntity.ok(queryUseCase.getActive().stream().map(mapper::toDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OfferingDto> getOffering(@PathVariable String id) {
        return queryUseCase.getById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
