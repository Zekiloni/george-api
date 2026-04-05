package com.zekiloni.george.platform.infrastructure.out.persistence.lead.adapter;

import com.zekiloni.george.platform.application.port.out.LeadRepositoryPort;
import com.zekiloni.george.platform.domain.lead.model.Lead;
import com.zekiloni.george.platform.infrastructure.out.persistence.lead.mapper.LeadEntityMapper;
import com.zekiloni.george.platform.infrastructure.out.persistence.lead.repository.LeadJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LeadRepositoryAdapter implements LeadRepositoryPort {
    private final LeadJpaRepository repository;
    private final LeadEntityMapper mapper;

    @Override
    public Lead save(Lead lead) {
        return mapper.toDomain(repository.save(mapper.toEntity(lead)));
    }

    @Override
    public List<Lead> saveAll(List<Lead> leads) {
        return mapper.toDomain(repository.saveAll(mapper.toEntity(leads)));
    }

    @Override
    public Page<Lead> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
