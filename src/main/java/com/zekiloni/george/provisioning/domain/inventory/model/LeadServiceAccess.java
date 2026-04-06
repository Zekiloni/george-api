package com.zekiloni.george.provisioning.domain.inventory.model;

import com.zekiloni.george.platform.domain.model.Lead;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LeadServiceAccess extends ServiceAccess {
    private List<Lead> leads;
}
