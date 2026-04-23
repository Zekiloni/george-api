package com.zekiloni.george.platform.infrastructure.in.web;

import com.zekiloni.george.platform.application.port.in.LeadImportUseCase;
import com.zekiloni.george.platform.application.port.in.LeadQueryUseCase;
import com.zekiloni.george.platform.infrastructure.in.web.dto.LeadDto;
import com.zekiloni.george.platform.infrastructure.in.web.mapper.LeadDtoMapper;
import com.zekiloni.george.platform.infrastructure.out.persistence.lead.entity.LeadSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.base.path:/api/v1}/lead")
public class LeadApiController {
    private final LeadImportUseCase importUseCase;
    private final LeadQueryUseCase queryUseCase;
    private final LeadDtoMapper mapper;


    @PreAuthorize("hasRole('admin') or @serviceAccessQueryUseCase.hasActiveAccess(T(com.zekiloni.george.commerce.domain.catalog.model.ServiceSpecification).PAGE)")
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> importLeads(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "serviceAccessId", required = false) String serviceAccessId) throws IOException {
        importUseCase.handle(new LeadImportUseCase.LeadImportCommand(file.getInputStream(), Optional.ofNullable(serviceAccessId)));
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<Page<LeadDto>> listLeads(Pageable pageable, LeadSpecification specification) {
        return ResponseEntity.ok(queryUseCase.handle(pageable, specification).map(mapper::toDto));
    }
}
