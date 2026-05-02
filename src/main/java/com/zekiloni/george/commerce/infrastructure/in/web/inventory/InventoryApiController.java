package com.zekiloni.george.commerce.infrastructure.in.web.inventory;

import com.zekiloni.george.commerce.application.port.in.ServiceAccessCancelUseCase;
import com.zekiloni.george.commerce.application.port.in.ServiceAccessQueryUseCase;
import com.zekiloni.george.commerce.application.port.in.ServiceAccessRenewalControlUseCase;
import com.zekiloni.george.commerce.infrastructure.in.web.inventory.dto.ServiceAccessDto;
import com.zekiloni.george.commerce.infrastructure.in.web.inventory.mapper.ServiceAccessDtoMapper;
import com.zekiloni.george.commerce.infrastructure.out.persistence.inventory.entity.ServiceAccessSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.base.path:/api/v1}/inventory")
@RequiredArgsConstructor
public class InventoryApiController {
    private final ServiceAccessQueryUseCase queryUseCase;
    private final ServiceAccessCancelUseCase cancelUseCase;
    private final ServiceAccessRenewalControlUseCase renewalControlUseCase;
    private final ServiceAccessDtoMapper mapper;

    @GetMapping
    public ResponseEntity<Page<ServiceAccessDto>> getServiceAccesses(Pageable pageable, ServiceAccessSpecification specification) {
        return ResponseEntity.ok(queryUseCase.getAll(specification, pageable).map(mapper::toDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceAccessDto> getServiceAccess(@PathVariable String id) {
        return queryUseCase.getById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('admin')")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelServiceAccess(@PathVariable String id) {
        cancelUseCase.cancel(id);
        return ResponseEntity.noContent().build();
    }

    /** Customer-facing: stop auto-renewal so the service expires at validTo. */
    @PostMapping("/{id}/cancel-renewal")
    public ResponseEntity<Void> cancelRenewal(@PathVariable String id) {
        renewalControlUseCase.setCancelAtPeriodEnd(id, true);
        return ResponseEntity.noContent().build();
    }

    /** Customer-facing: undo a previous cancel-renewal request. */
    @PostMapping("/{id}/resume-renewal")
    public ResponseEntity<Void> resumeRenewal(@PathVariable String id) {
        renewalControlUseCase.setCancelAtPeriodEnd(id, false);
        return ResponseEntity.noContent().build();
    }
}
