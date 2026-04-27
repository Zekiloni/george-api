package com.zekiloni.george.commerce.infrastructure.out.persistence.inventory.entity;

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.In;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@And({
        @Spec(path = "status", spec = Equal.class),
        @Spec(path = "serviceSpecification", params = "serviceSpecification", spec = In.class)
})
public interface ServiceAccessSpecification extends Specification<ServiceAccessEntity> {
}
