package com.zekiloni.george.workspace.infrastructure.input.web.page;


import com.zekiloni.george.workspace.application.port.in.PageCreateUseCase;
import com.zekiloni.george.workspace.application.port.in.PageDeleteUseCase;
import com.zekiloni.george.workspace.application.port.in.PageQueryUseCase;
import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.PageCreateDto;
import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.PageDto;
import com.zekiloni.george.workspace.infrastructure.input.web.page.mapper.PageDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.base.path:/api/v1}/page")
@RequiredArgsConstructor
public class PageApiController {
    private final PageDtoMapper mapper;
    private final PageCreateUseCase pcreateUseCase;
    private final PageQueryUseCase queryUseCase;
    private final PageDeleteUseCase deleteUseCase;

    @PostMapping
    public ResponseEntity<PageDto> create(@RequestBody @Valid PageCreateDto pageCreate) {
        return ResponseEntity.ok(mapper.toDto(pcreateUseCase.handle(mapper.toDomain(pageCreate))));
    }

    @GetMapping
    private ResponseEntity<Page<PageDto>> getAll(Pageable pageable) {
        return ResponseEntity.ok(queryUseCase.handle(pageable).map(mapper::toDto));
    }

    @GetMapping("/{id}")
    private ResponseEntity<PageDto> getById(@PathVariable String id) {
        return queryUseCase.handle(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteById(@PathVariable String id) {
        deleteUseCase.handle(id);
        return ResponseEntity.noContent().build();
    }
}
