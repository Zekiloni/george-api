package com.zekiloni.george.commerce.domain.inventory.model;

import com.zekiloni.george.platform.domain.model.Lead;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LeadServiceAccess extends ServiceAccess {
    private List<Lead> leads;

    public void addLeads(List<String> leadIds) {
        if (leads == null) leads = new ArrayList<>();
        leadIds.forEach(leadId -> {
            if (leads.stream().map(Lead::getId).filter(Objects::nonNull).noneMatch(leadId::equals)) {
                leads.add(Lead.builder().id(leadId).build());
            }
        });
    }
}
