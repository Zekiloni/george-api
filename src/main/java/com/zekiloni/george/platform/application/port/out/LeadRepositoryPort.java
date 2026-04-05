package com.zekiloni.george.platform.application.port.out;

import com.zekiloni.george.platform.domain.lead.model.Lead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LeadRepositoryPort {
    Lead save(Lead lead);
    List<Lead> saveAll(List<Lead> leads);

    Page<Lead> findAll(Pageable pageable);

    void deleteById(String id);
}
