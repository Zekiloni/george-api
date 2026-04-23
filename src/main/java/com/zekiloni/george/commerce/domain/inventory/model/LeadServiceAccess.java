package com.zekiloni.george.commerce.domain.inventory.model;

import com.zekiloni.george.platform.domain.model.Lead;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LeadServiceAccess extends ServiceAccess {
    private List<Lead> leads;

    public void addLeads(List<String> leadIds) {
            leadIds.forEach(leadId -> {
                if (leads.stream().noneMatch(lead -> lead.getId().equals(leadId))) {
                    leads.add(Lead.builder().id(leadId).build());
                }
            });
    }
}
