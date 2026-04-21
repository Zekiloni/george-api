package com.zekiloni.george.commerce.infrastructure.out.persistence.order.entity;

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@And({
        @Spec(path = "status", spec = Equal.class),
})
public interface InvoiceSpecification extends Specification<InvoiceEntity> {
}
