package com.zekiloni.george.commerce.domain.inventory.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LeadServiceAccess extends ServiceAccess {
    @Builder.Default
    private List<String> leadIds = new ArrayList<>();

    public void addLeads(List<String> newLeadIds) {
        for (String leadId : newLeadIds) {
            if (!leadIds.contains(leadId)) {
                leadIds.add(leadId);
            }
        }
    }
}
