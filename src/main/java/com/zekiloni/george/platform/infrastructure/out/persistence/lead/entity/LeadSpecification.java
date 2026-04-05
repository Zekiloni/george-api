package com.zekiloni.george.platform.infrastructure.out.persistence.lead.entity;

import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@And({
        @Spec(path = "location", spec = LikeIgnoreCase.class),
        @Spec(path = "phoneNumber", spec = LikeIgnoreCase.class),
        @Spec(path = "country", spec = LikeIgnoreCase.class),
        @Spec(path = "areaCode", spec = LikeIgnoreCase.class)
})
public interface LeadSpecification extends Specification<LeadEntity> {
}