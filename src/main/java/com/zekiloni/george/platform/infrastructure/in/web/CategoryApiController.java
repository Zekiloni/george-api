package com.zekiloni.george.platform.infrastructure.in.web;

import com.zekiloni.george.platform.application.port.in.template.CategoryMutationUseCase;
import com.zekiloni.george.platform.application.port.in.template.CategoryQueryUseCase;
import com.zekiloni.george.platform.infrastructure.in.web.dto.template.CategoryDto;
import com.zekiloni.george.platform.infrastructure.in.web.dto.template.CategoryUpsertDto;
import com.zekiloni.george.platform.infrastructure.in.web.mapper.TemplateDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.base.path:/api/v1}/categories")
@RequiredArgsConstructor
public class CategoryApiController {

    private final CategoryQueryUseCase queryUseCase;
    private final CategoryMutationUseCase mutationUseCase;
    private final TemplateDtoMapper mapper;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> list(@RequestParam(defaultValue = "false") boolean tree) {
        return ResponseEntity.ok(mapper.toCategoryDtos(
                tree ? queryUseCase.findTree() : queryUseCase.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> findById(@PathVariable String id) {
        return queryUseCase.findById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDto> create(@Valid @RequestBody CategoryUpsertDto dto) {
        return ResponseEntity.ok(mapper.toDto(mutationUseCase.create(mapper.toDomain(dto))));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDto> update(@PathVariable String id,
                                              @Valid @RequestBody CategoryUpsertDto dto) {
        return ResponseEntity.ok(mapper.toDto(mutationUseCase.update(id, mapper.toDomain(dto))));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        mutationUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
