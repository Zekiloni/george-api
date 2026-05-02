package com.zekiloni.george.commerce.infrastructure.out.persistence.order.adapter;

import com.zekiloni.george.commerce.application.port.out.InvoiceRepositoryPort;
import com.zekiloni.george.commerce.domain.order.model.invoice.Invoice;
import com.zekiloni.george.commerce.infrastructure.out.persistence.order.entity.InvoiceSpecification;
import com.zekiloni.george.commerce.infrastructure.out.persistence.order.mapper.InvoiceEntityMapper;
import com.zekiloni.george.commerce.infrastructure.out.persistence.order.repository.InvoiceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InvoiceRepositoryPortAdapter implements InvoiceRepositoryPort {
    private final InvoiceJpaRepository repository;
    private final InvoiceEntityMapper mapper;

    @Override
    public Optional<Invoice> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Invoice> findByOrderId(UUID orderId) {
        return repository.findByOrderId(orderId)
                .map(mapper::toDomain);
    }

    @Override
    public Invoice save(Invoice invoice) {
        return mapper.toDomain(repository.save(mapper.toEntity(invoice)));
    }

    @Override
    public Page<Invoice> findAll(Pageable pageable, InvoiceSpecification specification) {
        return repository.findAll(specification, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public boolean hasOpenRenewalForServiceAccess(String serviceAccessId) {
        if (serviceAccessId == null) return false;
        return repository.existsOpenRenewalForServiceAccess(serviceAccessId);
    }

    @Override
    public Optional<Invoice> findByInvoiceNumber(String invoiceNumber) {
        if (invoiceNumber == null) return Optional.empty();
        return repository.findByInvoiceNumber(invoiceNumber).map(mapper::toDomain);
    }
}
