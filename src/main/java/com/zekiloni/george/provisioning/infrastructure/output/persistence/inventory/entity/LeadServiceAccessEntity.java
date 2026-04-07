package com.zekiloni.george.provisioning.infrastructure.output.persistence.inventory.entity;

import com.zekiloni.george.platform.infrastructure.out.persistence.lead.entity.LeadEntity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LeadServiceAccessEntity extends ServiceAccessEntity {

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "lead_service_access_leads",
            joinColumns = @JoinColumn(name = "service_access_id"),
            inverseJoinColumns = @JoinColumn(name = "lead_id")
    )
    private Set<LeadEntity> leads = new HashSet<>();
}
