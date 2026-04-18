package com.zekiloni.george.workspace.infrastructure.input.web.page;

import com.zekiloni.george.workspace.application.port.in.PageCreateUseCase;
import com.zekiloni.george.workspace.application.port.in.PageDeleteUseCase;
import com.zekiloni.george.workspace.application.port.in.PageQueryUseCase;
import com.zekiloni.george.workspace.application.port.in.PageUpdateUseCase;

import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.PageCreateDto;
import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.PageDto;

import com.zekiloni.george.workspace.infrastructure.input.web.page.mapper.PageDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.base.path:/api/v1}/page")
@RequiredArgsConstructor
public class PageApiController {

    private final PageDtoMapper webDtoMapper;
    private final PageCreateUseCase createUseCase;
    private final PageQueryUseCase queryUseCase;
    private final PageUpdateUseCase updateUseCase;
    private final PageDeleteUseCase deleteUseCase;

    @PostMapping
    public ResponseEntity<PageDto> create(
        @RequestBody @Valid PageCreateDto pageCreate
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(webDtoMapper.toDto(webDtoMapper.toDomain(pageCreate)));
    }


    @GetMapping
    public ResponseEntity<Page<PageDto>> findAll(Pageable pageable) {
        return ResponseEntity.ok(queryUseCase.findAll(pageable).map(webDtoMapper::toDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PageDto> findById(@PathVariable String id) {
        return queryUseCase.findById(id)
            .map(webDtoMapper::toDto)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<PageDto> findBySlug(@PathVariable String slug) {
        return queryUseCase.findBySlug(slug)
            .map(webDtoMapper::toDto)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        deleteUseCase.handle(id);
        return ResponseEntity.noContent().build();
    }
}
