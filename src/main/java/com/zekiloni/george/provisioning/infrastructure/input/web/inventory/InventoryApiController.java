package com.zekiloni.george.provisioning.infrastructure.input.web.inventory;

import com.zekiloni.george.provisioning.application.port.in.ServiceAccessQueryUseCase;
import com.zekiloni.george.provisioning.infrastructure.input.web.inventory.dto.ServiceAccessDto;
import com.zekiloni.george.provisioning.infrastructure.input.web.inventory.mapper.ServiceAccessDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.base.path:/api/v1}/inventory")
@RequiredArgsConstructor
public class InventoryApiController {
    private final ServiceAccessQueryUseCase queryUseCase;
    private final ServiceAccessDtoMapper mapper;

    @GetMapping
    public ResponseEntity<Page<ServiceAccessDto>> getServiceAccesses(Pageable pageable) {
        return ResponseEntity.ok(queryUseCase.getAll(pageable).map(mapper::toDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceAccessDto> getServiceAccess(@PathVariable String id) {
        return queryUseCase.getById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}

