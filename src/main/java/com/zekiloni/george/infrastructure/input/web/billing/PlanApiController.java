package com.zekiloni.george.infrastructure.input.web.billing;

import com.zekiloni.george.application.port.in.PlanCreateUseCase;
import com.zekiloni.george.application.port.in.PlanQueryUseCase;
import com.zekiloni.george.infrastructure.input.web.billing.dto.PlanCreateDto;
import com.zekiloni.george.infrastructure.input.web.billing.dto.PlanDto;
import com.zekiloni.george.infrastructure.input.web.billing.mapper.PlanDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.base.path:/api/v1}/plan")
@RequiredArgsConstructor
public class PlanApiController {
    private final PlanCreateUseCase createUseCase;
    private final PlanQueryUseCase queryUseCase;
    private final PlanDtoMapper mapper;

    @PostMapping
    public ResponseEntity<PlanDto> createPlan(@RequestBody PlanCreateDto planCreate) {
        return ResponseEntity.ok(mapper.toDto(createUseCase.create(mapper.toDomain(planCreate))));
    }

    @GetMapping
    public ResponseEntity<Page<PlanDto>> getPlans(Pageable pageable) {
        return ResponseEntity.ok(queryUseCase.getAll(pageable).map(mapper::toDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanDto> getPlan(@PathVariable String id) {
        return queryUseCase.getById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
