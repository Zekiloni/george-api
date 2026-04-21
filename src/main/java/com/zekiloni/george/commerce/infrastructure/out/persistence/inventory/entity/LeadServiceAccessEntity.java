package com.zekiloni.george.commerce.infrastructure.out.persistence.inventory.entity;

import com.zekiloni.george.platform.infrastructure.out.persistence.lead.entity.LeadEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lead_service_access")
public class LeadServiceAccessEntity extends ServiceAccessEntity {

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "lead_service_access_leads",
            joinColumns = @JoinColumn(name = "service_access_id"),
            inverseJoinColumns = @JoinColumn(name = "lead_id")
    )
    private Set<LeadEntity> leads = new java.util.HashSet<>();
}
