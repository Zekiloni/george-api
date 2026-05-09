package com.zekiloni.george.platform.infrastructure.in.web;

import com.zekiloni.george.platform.application.port.in.page.PageTemplateUseCase;
import com.zekiloni.george.platform.domain.model.page.Page;
import com.zekiloni.george.platform.domain.model.page.PageTemplate;
import com.zekiloni.george.platform.domain.service.page.HtmlTemplateImporter;
import com.zekiloni.george.platform.infrastructure.in.web.dto.page.PageDto;
import com.zekiloni.george.platform.infrastructure.in.web.dto.page.PageTemplateDto;
import com.zekiloni.george.platform.infrastructure.in.web.mapper.PageDtoMapper;
import com.zekiloni.george.platform.infrastructure.in.web.mapper.PageTemplateDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("${api.base.path:/api/v1}/page-templates")
@RequiredArgsConstructor
public class PageTemplateApiController {

    private final PageTemplateUseCase useCase;
    private final PageTemplateDtoMapper templateMapper;
    private final PageDtoMapper pageMapper;
    private final HtmlTemplateImporter htmlImporter;

    @GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<PageTemplateDto>> findAll(Pageable pageable) {
        return ResponseEntity.ok(useCase.findAll(pageable).map(templateMapper::toDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PageTemplateDto> findById(@PathVariable String id) {
        return useCase.findById(id)
                .map(templateMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('admin')")
    @PostMapping("/{id}/clone")
    public ResponseEntity<PageDto> clone(@PathVariable String id,
                                         @RequestParam(required = false) String slug,
                                         @AuthenticationPrincipal Jwt jwt) {
        Page page = useCase.cloneToPage(id, slug, jwt != null ? jwt.getSubject() : null);
        return ResponseEntity.status(HttpStatus.CREATED).body(pageMapper.toDto(page));
    }

    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        useCase.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Parse an uploaded HTML file into a PageTemplate shape. Nothing is saved —
    // the response is a draft template the page builder can use to seed its
    // form. Auto-detected: {{var}} placeholders → manifest fields,
    // <form>/<input> elements → form config, <style> blocks → css.
    @PreAuthorize("hasRole('admin')")
    @PostMapping(value = "/import", consumes = "multipart/form-data")
    public ResponseEntity<PageTemplateDto> importHtml(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        String html = new String(file.getBytes(), StandardCharsets.UTF_8);
        PageTemplate template = htmlImporter.parse(html, file.getOriginalFilename());
        return ResponseEntity.ok(templateMapper.toDto(template));
    }
}
