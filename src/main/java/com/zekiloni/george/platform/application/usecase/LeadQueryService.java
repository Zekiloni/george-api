package com.zekiloni.george.platform.application.usecase;

import com.zekiloni.george.platform.application.port.in.LeadQueryUseCase;
import com.zekiloni.george.platform.application.port.out.LeadRepositoryPort;
import com.zekiloni.george.platform.domain.lead.model.Lead;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LeadQueryService implements LeadQueryUseCase {
    private final LeadRepositoryPort repository;

    @Override
    public Page<Lead> handle(Pageable pageable) {
        return repository.findAll(pageable);
    }
}
