package com.zekiloni.george.platform.application.port.out.lead;

import com.zekiloni.george.platform.domain.model.lead.Lead;
import com.zekiloni.george.platform.infrastructure.out.persistence.lead.entity.LeadSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

public interface LeadRepositoryPort {
    Lead save(Lead lead);
    List<Lead> saveAll(List<Lead> leads);
    Page<Lead> findAll(Pageable pageable, LeadSpecification specification);
    void deleteById(String id);

    /**
     * Random-order pick of {@code limit} leads matching any provided filter.
     * Null filters are ignored; passing all-null returns a fully random sample.
     */
    List<Lead> findRandom(LeadFilter filter, int limit);

    /**
     * Bulk lookup by phone number. Used at campaign creation to stamp
     * {@code carrier} / {@code country} onto outreaches.
     */
    List<Lead> findByPhoneNumberIn(Collection<String> phoneNumbers);

    record LeadFilter(String country, String areaCode, String regionCode,
                      String carrier, String location) {
        public static LeadFilter empty() {
            return new LeadFilter(null, null, null, null, null);
        }
    }
}
